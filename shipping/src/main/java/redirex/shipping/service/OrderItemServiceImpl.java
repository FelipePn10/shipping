package redirex.shipping.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final WarehouseService warehouseService;
    private final UserWalletService userWalletService;
    private final UserRepository userRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderDistributionService orderDistributionService;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    @Override
    @Transactional
    public OrderItemResponse createOrderItem(Long userId, @Valid CreateOrderItemRequest request) {
        logger.info("Creating order: {}", request.getProductUrl());

        UserEntity user = userRepository.findById(request.getUserId().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        ProductCategoryEntity category = productCategoryRepository.findById(request.getProductCategoryId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));

        WarehouseEntity warehouse = warehouseRepository.findById(request.getWarehouseId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found."));

        // Map request to entity
        OrderItemEntity orderItem = mapRequestToEntity(request, user, category, warehouse);

        // Save order with initial status CREATING_ORDER
        orderItem = orderItemRepository.save(orderItem);
        logger.info("Order created - ID: {}", orderItem.getId());

        return mapEntityToResponse(orderItem);
    }

    @Override
    @Transactional
    public OrderItemResponse processOrderPayment(Long orderItemId, Long userId) {
        OrderItemEntity orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));

        if (!orderItem.getUserId().getId().equals(userId)) {
            throw new AccessDeniedException("Order does not belong to user");
        }

        if (orderItem.getStatus() != OrderItemStatusEnum.CREATING_ORDER) {
            logger.warn("Order {} is not in CREATING_ORDER status, current status: {}",
                    orderItemId, orderItem.getStatus());
            throw new IllegalStateException("Order is not in a payable state.");
        }

        UserEntity user = orderItem.getUserId();
        if (user == null) {
            logger.error("User not associated with order: {}", orderItemId);
            throw new IllegalStateException("User not associated with order.");
        }

        try {
            // Process payment with retry
            processPaymentWithRetry(user, orderItem);

            // Update status to PAID
            orderItem.setStatus(OrderItemStatusEnum.PAID);

            // Assign to the least busy admin
            AdminEntity assignedAdmin = orderDistributionService.assignToLeastBusyAdmin();
            orderItem.setAdminAssigned(assignedAdmin);

            // Add order to warehouse
            warehouseService.addOrderItemToWarehouse(
                    orderItem.getId(),
                    orderItem.getWarehouseId().getId()
            );

            // Save updated order
            orderItem = orderItemRepository.save(orderItem);
            logger.info("Payment processed for order: {}", orderItem.getId());

        } catch (InsufficientBalanceException ex) {
            // Handle payment failure due to insufficient balance
            handlePaymentFailure(orderItem, "Insufficient balance");
            throw ex;
        } catch (Exception ex) {
            // Handle other payment failures (after retries)
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
            // Repass exception for specific handling
            throw ex;
        } catch (Exception ex) {
            logger.warn("Payment attempt failed for order: {}. Trying again, please wait!",
                    orderItem.getId(), ex);
            throw ex; // Rethrow for retry mechanism
        }
    }

    private void handlePaymentFailure(OrderItemEntity orderItem, String reason) {
        orderItem.setStatus(OrderItemStatusEnum.PAYMENT_FAILED);
        orderItemRepository.save(orderItem);
        logger.error("Payment failed for order: {}. Reason: {}",
                orderItem.getId(), reason);
    }

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
                .status(OrderItemStatusEnum.CREATING_ORDER)
                .warehouseId(warehouse)
                .build();

        orderItem.setWarehouse(warehouse);
        return orderItem;
    }

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