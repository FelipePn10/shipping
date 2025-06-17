package redirex.shipping.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.entity.*;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.OrderItemStatusEnum;
import redirex.shipping.exception.InsufficientBalanceException;
import redirex.shipping.exception.PaymentProcessingException;
import redirex.shipping.repositories.OrderItemRepository;
import redirex.shipping.repositories.ProductCategoryRepository;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.repositories.WarehouseRepository;
import redirex.shipping.service.admin.OrderDistributionService;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemServiceImpl.class);

    private final OrderItemRepository orderItemRepository;
    private final UserWalletService userWalletService;
    private final UserRepository userRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderDistributionService orderDistributionService;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    @Override
    @Transactional
    public OrderItemResponse createOrderItem(@Valid CreateOrderItemRequest request) {
        logger.info("Creating order: {}", request.getProductUrl());

        UserEntity user = userRepository.findById(request.getUserId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        ProductCategoryEntity category = productCategoryRepository.findById(request.getProductCategoryId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        WarehouseEntity warehouse = warehouseRepository.findById(request.getWarehouseId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Armazém não encontrado"));

        // Mapeia request -> entity
        OrderItemEntity orderItem = mapRequestToEntity(request, user, category, warehouse);

        // E Salva o pedido (status inicial = CREATED)
        orderItem = orderItemRepository.save(orderItem);
        logger.info("Order created - ID: {}", orderItem.getId());

        try {
            // Processa pagamento com retentativas
            processPaymentWithRetry(user, orderItem);

            // Atualizar status para PAGO se tudo ocorrer bem
            orderItem.setStatus(OrderItemStatusEnum.PAID);

            AdminEntity assignedAdmin = orderDistributionService.assignToLeastBusyAdmin();
            orderItem.setAdminAssigned(assignedAdmin);

            warehouse.getOrderItems().add(orderItem); // Adiciona o item a warehouse
            warehouseRepository.save(warehouse); // Atualiza a warehouse

            orderItem = orderItemRepository.save(orderItem);
            logger.info("Payment processed for order: {}", orderItem.getId());

        } catch (InsufficientBalanceException ex) {
            // Tratar falha de pagamento por saldo insuficiente
            handlePaymentFailure(orderItem, "Insufficient balance");
            throw ex;
        } catch (Exception ex) {
            // Tratar outras falhas (após retentativas)
            handlePaymentFailure(orderItem, "Payment processing failed");
            throw new PaymentProcessingException("Redirect to deposit screen", ex);
        }

        // resposta detalhada
        return mapEntityToResponse(orderItem);
    }


     // Tenta até 3 vezes com backoff exponencial
    @Retryable(
            value = {Exception.class},
            exclude = {InsufficientBalanceException.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private void processPaymentWithRetry(UserEntity user, OrderItemEntity orderItem)
            throws InsufficientBalanceException {

        try {
            userWalletService.debitFromWallet(
                    user.getId(),
                    CurrencyEnum.CNY,
                    orderItem.getProductValue(),
                    "ORDER_PAYMENT",
                    "Payment of the order: " + orderItem.getId(),
                    orderItem.getId(),
                    null
            );
        } catch (InsufficientBalanceException ex) {
            // Repassa exceção para tratamento específico
            throw ex;
        } catch (Exception ex) {
            logger.warn("Payment attempt failed for order: {}. Trying again, please wait!",
                    orderItem.getId(), ex);
            throw ex; // Relança para o mecanismo de retry
        }
    }

    private void handlePaymentFailure(OrderItemEntity orderItem, String reason) {
        orderItem.setStatus(OrderItemStatusEnum.PAYMENT_FAILED);
        orderItemRepository.save(orderItem);
        logger.error("Payment failed for order: {}. Reason: {}",
                orderItem.getId(), reason);
    }

    // Mapeamento request -> entity
    private OrderItemEntity mapRequestToEntity(CreateOrderItemRequest request,
                                               UserEntity user,
                                               ProductCategoryEntity category,
                                               WarehouseEntity warehouse) {
        OrderItemEntity orderItem = OrderItemEntity.builder()
                .userId(user)
                .productUrl(request.getProductUrl())
                .description(request.getDescription())
                .size(request.getSize())
                .productValue(request.getProductValue())
                .category(category)
                .recipientCpf(String.valueOf(request.getRecipientCpf()))
                .status(OrderItemStatusEnum.CREATING_ORDER) // Status inicial
                .warehouseId(warehouse)
                .build();

        orderItem.setWarehouse(warehouse);
        return orderItem;
    }

    // Mapeamento entity -> response
    private OrderItemResponse mapEntityToResponse(OrderItemEntity entity) {
        return OrderItemResponse.builder()
                .id(entity.getId())
                .productUrl(entity.getProductUrl())
                .description(entity.getDescription())
                .size(entity.getSize())
                .productValue(entity.getProductValue())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}