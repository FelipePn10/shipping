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
import redirex.shipping.exception.OrderCreationFailedException;
import redirex.shipping.exception.PaymentProcessingException;
import redirex.shipping.repositories.OrderItemRepository;
import redirex.shipping.service.admin.OrderDistributionService;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemServiceImpl.class);

    private final OrderItemRepository orderItemRepository;
    private final WarehouseService warehouseService;
    private final UserWalletService userWalletService;
    private final OrderDistributionService orderDistributionService;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    @Override
    @Transactional
    public OrderItemResponse createOrderItem(Long userId, @Valid CreateOrderItemRequest request, UserEntity user, ProductCategoryEntity category, WarehouseEntity warehouse) {
        logger.info("Creating order for userId: {}, productUrl: {}", userId, request.getProductUrl());

        // Map request to entity
        OrderItemEntity orderItem = mapRequestToEntity(request, user, category, warehouse);

        // Save order with initial status CREATING_ORDER
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
    public OrderItemResponse processOrderPayment(Long orderItemId, Long userId) {
        OrderItemEntity orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderItemId));

        if (!orderItem.getUserId().getId().equals(userId)) {
            logger.warn("Order {} does not belong to user {}", orderItemId, userId);
            throw new AccessDeniedException("Order does not belong to user");
        }

        if (orderItem.getStatus() != OrderItemStatusEnum.CREATING_ORDER) {
            logger.warn("Order {} is not in CREATING_ORDER status, current status: {}", orderItemId, orderItem.getStatus());
            throw new IllegalStateException("Order is not in a payable state");
        }

        UserEntity user = orderItem.getUserId();
        if (user == null) {
            logger.error("User not associated with order: {}", orderItemId);
            throw new IllegalStateException("User not associated with order");
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
            warehouseService.addOrderItemToWarehouse(orderItem.getId(), orderItem.getWarehouseId().getId());

            // Save updated order
            orderItem = orderItemRepository.save(orderItem);
            logger.info("Payment processed for order: {}", orderItem.getId());

        } catch (InsufficientBalanceException ex) {
            handlePaymentFailure(orderItem, "Insufficient balance");
            throw ex;
        } catch (Exception ex) {
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
    private void processPaymentWithRetry(UserEntity user, OrderItemEntity orderItem) throws InsufficientBalanceException {
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
            throw ex;
        } catch (Exception ex) {
            logger.warn("Payment attempt failed for order: {}. Retrying...", orderItem.getId(), ex);
            throw ex;
        }
    }

    private void handlePaymentFailure(OrderItemEntity orderItem, String reason) {
        orderItem.setStatus(OrderItemStatusEnum.PAYMENT_FAILED);
        orderItemRepository.save(orderItem);
        logger.error("Payment failed for order: {}. Reason: {}", orderItem.getId(), reason);
    }

    private OrderItemEntity mapRequestToEntity(CreateOrderItemRequest request, UserEntity user, ProductCategoryEntity category, WarehouseEntity warehouse) {
        return OrderItemEntity.builder()
                .userId(user)
                .productUrl(request.getProductUrl())
                .description(request.getDescription())
                .size(request.getSize())
                .quantity(request.getQuantity())
                .productValue(request.getProductValue())
                .category(category)
                .recipientCpf(request.getRecipientCpf())
                .status(OrderItemStatusEnum.CREATING_ORDER)
                .warehouseId(warehouse)
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
                .categoryName(entity.getCategory().getName()) // Assumindo que ProductCategoryEntity tem um campo name
                .recipientCpf(entity.getRecipientCpf())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .paymentDeadline(entity.getPaymentDeadline())
                .paidProductAt(entity.getPaidProductAt())
                .arrivedAtWarehouseAt(entity.getArrivedAtWarehouseAt())
                .shipmentId(entity.getShipmentId() != null ? entity.getShipmentId().getId() : null)
                .build();
    }
}