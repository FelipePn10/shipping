package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.exception.OrderCreationFailedException;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.OrderItemService;

@RestController
@RequestMapping("/private/v1/api/users")
@RequiredArgsConstructor
public class OrderItemController {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemController.class);

    private final OrderItemService orderItemService;
    private final JwtUtil jwtUtil;

    @PostMapping("/{userId}/create/order")
    public ResponseEntity<OrderItemResponse> createOrder(
            @PathVariable Long userId,
            @Valid @RequestBody CreateOrderItemRequest request) {

        logger.info("Creating order for userId: {}, request: {}", userId, request);

        try {
            // Validação de consistência de IDs
            if (!userId.equals(request.getUserId())) {
                logger.warn("User ID mismatch: path={}, body={}", userId, request.getUserId());
                throw new IllegalArgumentException("User ID in path must match request body");
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            // Extrair o ID do usuário do token corretamente
            Long authenticatedUserId = jwtUtil.getUserIdFromUsername(username);
            if (authenticatedUserId == null || !authenticatedUserId.equals(userId)) {
                logger.warn("Authorization failed: authUserId={}, pathUserId={}", authenticatedUserId, userId);
                throw new SecurityException("User not authorized for this operation");
            }

            // Criação do pedido
            OrderItemResponse response = orderItemService.createOrderItem(userId, request);
            logger.info("Order created successfully. ID: {}", response.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException | SecurityException ex) {
            logger.error("Validation error: {}", ex.getMessage());
            throw new OrderCreationFailedException("Validation failed: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error: {}", ex.getMessage(), ex);
            throw new OrderCreationFailedException("Internal server error: " + ex.getMessage());
        }
    }
}