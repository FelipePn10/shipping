package redirex.shipping.service;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.entity.*;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.OrderItemStatusEnum;
import redirex.shipping.exception.InsufficientBalanceException;
import redirex.shipping.exception.OrderCreationFailedException;
import redirex.shipping.exception.PaymentProcessingException;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.repositories.OrderItemRepository;
import redirex.shipping.repositories.ProductCategoryRepository;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.repositories.WarehouseRepository;
import redirex.shipping.service.admin.OrderDistributionService;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemServiceImpl.class);

    private final OrderItemRepository orderItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final WarehouseService warehouseService;
    private final UserWalletService userWalletService;
    private final OrderDistributionService orderDistributionService;
    private final WebScrapingService webScrapingService;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, WarehouseRepository warehouseRepository, UserRepository userRepository, ProductCategoryRepository productCategoryRepository, WarehouseService warehouseService, UserWalletService userWalletService, OrderDistributionService orderDistributionService, WebScrapingService webScrapingService) {
        this.orderItemRepository = orderItemRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.warehouseService = warehouseService;
        this.userWalletService = userWalletService;
        this.orderDistributionService = orderDistributionService;
        this.webScrapingService = webScrapingService;
    }

    @Override
    @Transactional
    public OrderItemResponse createOrderItem(UUID userId, @Valid CreateOrderItemRequest request) {
        logger.info("Creating order for userId: {}, productUrl: {}", userId, request.getProductUrl());
        BigDecimal productPrice;

        // Buscar entidades dentro da transação
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        WarehouseEntity warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + request.getWarehouseId()));

        ProductCategoryEntity category = productCategoryRepository.findById(request.getProductCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with ID: " + request.getProductCategoryId()));

        // Capturar preço do produto via scraping
        if (request.isAutoFetchPrice()) {
            try {
                productPrice = webScrapingService.scrapeProductPrice(request.getProductUrl());
            } catch (Exception e) {
                throw new OrderCreationFailedException("Falha ao obter preço automático: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Captura automática de preço é obrigatória");
        }

        productPrice = productPrice.setScale(2, java.math.RoundingMode.HALF_UP);

        // Mapear request para entidade com o preço capturado
        OrderItemEntity orderItem = mapRequestToEntity(request, user, category, warehouse, productPrice);

        try {
            orderItem = orderItemRepository.save(orderItem);
            logger.info("Order created - ID: {}", orderItem.getId());
        } catch (Exception ex) {
            logger.error("Failed to save order item: {}", ex.getMessage(), ex);
            throw new OrderCreationFailedException("Failed to save order item: " + ex.getMessage(), ex);
        }

        return mapEntityToResponse(orderItem);
    }

    @Override
    @Transactional
    public OrderItemResponse processOrderPayment(UUID orderItemId, UUID userId) {
        OrderItemEntity orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderItemId));

        if (!orderItem.getUser().getId().equals(userId)) {
            logger.warn("Order {} does not belong to user {}", orderItemId, userId);
            throw new AccessDeniedException("Order does not belong to user");
        }

        if (orderItem.getStatus() != OrderItemStatusEnum.CREATING_ORDER) {
            logger.warn("Order {} is not in CREATING_ORDER status, current status: {}", orderItemId, orderItem.getStatus());
            throw new IllegalStateException("Order is not in a payable state");
        }

        try {
            // Garantindo que a escala antes de passar para o serviço de débito
            BigDecimal paymentAmount = orderItem.getProductValue().setScale(2, java.math.RoundingMode.HALF_UP);

            processPaymentWithRetry(
                    orderItem.getUser(),
                    paymentAmount,
                    orderItem.getId()
            );

            orderItem.setStatus(OrderItemStatusEnum.PAID);
            AdminEntity assignedAdmin = orderDistributionService.assignToLeastBusyAdmin();
            orderItem.setAdminAssigned(assignedAdmin);
            warehouseService.addOrderItemToWarehouse(orderItem.getId(), orderItem.getWarehouse().getId());
            orderItem = orderItemRepository.save(orderItem);

            logger.info("Payment processed for order: {}", orderItem.getId());
        } catch (InsufficientBalanceException ex) {
            handlePaymentFailure(orderItem, "Insufficient balance");
            throw ex;
        } catch (Exception ex) {
            logger.error("An unexpected error occurred during payment processing for order {}: {}", orderItemId, ex.getMessage(), ex);
            handlePaymentFailure(orderItem, "Payment processing failed");
            throw new PaymentProcessingException("Redirect to deposit screen", ex);
        }

        return mapEntityToResponse(orderItem);
    }

    @Retryable(
            value = {Exception.class},
            exclude = {InsufficientBalanceException.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private void processPaymentWithRetry(UserEntity user, BigDecimal amount, UUID orderItemId)
            throws InsufficientBalanceException {

        try {
            userWalletService.debitFromWallet(
                    user.getId(),                            // userId
                    CurrencyEnum.CNY,                        // currency (moeda da carteira)
                    amount,                                  // amount (valor a debitar)
                    "ORDER_PAYMENT",                         // transactionType
                    "Payment of the order: " + orderItemId,  // description
                    orderItemId,                             // relatedOrderItemId
                    null,                                    // shipmentId (corretamente nulo)
                    amount,                                  // NOVO: chargedAmount (valor da cobrança)
                    CurrencyEnum.CNY                         // NOVO: chargedCurrency (moeda da cobrança)
            );
        } catch (InsufficientBalanceException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.warn("Payment attempt failed for order: {}. Retrying...", orderItemId, ex);
            throw ex;
        }
    }

    private void handlePaymentFailure(OrderItemEntity orderItem, String reason) {
        orderItem.setStatus(OrderItemStatusEnum.PAYMENT_FAILED);
        orderItemRepository.save(orderItem);
        logger.error("Payment failed for order: {}. Reason: {}", orderItem.getId(), reason);
    }

    private OrderItemEntity mapRequestToEntity(CreateOrderItemRequest request, UserEntity user,
                                               ProductCategoryEntity category, WarehouseEntity warehouse,
                                               BigDecimal productPrice) {
        return OrderItemEntity.builder()
                .user(user)
                .productUrl(request.getProductUrl())
                .description(request.getDescription())
                .size(request.getSize())
                .quantity(request.getQuantity())
                .productValue(productPrice)
                .category(category)
                .recipientCpf(request.getRecipientCpf())
                .status(OrderItemStatusEnum.CREATING_ORDER)
                .warehouse(warehouse)
                .build();
    }

    private OrderItemResponse mapEntityToResponse(OrderItemEntity entity) {
        return OrderItemResponse.builder()
                .id(entity.getId())
                .productUrl(entity.getProductUrl())
                .description(entity.getDescription())
                .size(entity.getSize())
                .quantity(entity.getQuantity())
                .productValue(entity.getProductValue())
                .categoryName(entity.getCategory().getName())
                .recipientCpf(entity.getRecipientCpf())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .paymentDeadline(entity.getPaymentDeadline())
                .paidProductAt(entity.getPaidProductAt())
                .arrivedAtWarehouseAt(entity.getArrivedAtWarehouseAt())
                .shipmentId(entity.getShipment() != null ? entity.getShipment().getId() : null)
                .build();
    }
}