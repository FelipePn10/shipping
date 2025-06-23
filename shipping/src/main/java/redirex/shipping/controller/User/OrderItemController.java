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
import redirex.shipping.entity.ProductCategoryEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.WarehouseEntity;
import redirex.shipping.exception.OrderCreationFailedException;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.repositories.ProductCategoryRepository;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.repositories.WarehouseRepository;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.OrderItemService;

@RestController
@RequestMapping("/private/v1/api/users")
@RequiredArgsConstructor
public class OrderItemController {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemController.class);

    private final OrderItemService orderItemService;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/{userId}/create/order")
    public ResponseEntity<OrderItemResponse> createOrder(
            @PathVariable Long userId,
            @Valid @RequestBody CreateOrderItemRequest request) {
        logger.info("Creating order for userId: {}, request: {}", userId, request);

        try {
            // Validar userId no path e no corpo
            if (!userId.equals(request.getUserId())) {
                logger.warn("User ID in path ({}) does not match user ID in request body ({})", userId, request.getUserId());
                throw new IllegalArgumentException("User ID in path must match user ID in request body");
            }

            // Validar utilizador autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String jwt = auth.getCredentials().toString().replace("Bearer ", "");
            Long authenticatedUserId = jwtUtil.getUserIdFromToken(jwt);
            if (!authenticatedUserId.equals(userId)) {
                logger.warn("Authenticated user (ID: {}) not authorized to create order for userId: {}", authenticatedUserId, userId);
                throw new SecurityException("User not authorized to create order for another user");
            }

            // Buscar entidades
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
            WarehouseEntity warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + request.getWarehouseId()));
            ProductCategoryEntity category = productCategoryRepository.findById(request.getProductCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product category not found with ID: " + request.getProductCategoryId()));

            // Criar pedido
            OrderItemResponse response = orderItemService.createOrderItem(userId, request, user, category, warehouse);
            logger.info("Order created successfully. OrderId: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException | ResourceNotFoundException ex) {
            logger.error("Order creation failed: {}", ex.getMessage());
            throw new OrderCreationFailedException("Failed to create order: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            logger.error("Unexpected error during order creation: {}", ex.getMessage(), ex);
            throw new OrderCreationFailedException("Unexpected error during order creation: " + ex.getMessage(), ex);
        }
    }
}