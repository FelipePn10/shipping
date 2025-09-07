package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.ApiErrorResponse;
import redirex.shipping.dto.response.ApiResponse;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.exception.OrderCreationFailedException;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.OrderItemService;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/private/api/v1/users")
public class OrderItemController {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemController.class);

    private final OrderItemService orderItemService;
    private final JwtUtil jwtUtil;

    public OrderItemController(OrderItemService orderItemService, JwtUtil jwtUtil) {
        this.orderItemService = orderItemService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/{userId}/create/order")
    public ResponseEntity<ApiResponse<OrderItemResponse>> createOrder(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateOrderItemRequest request) {

        logger.info("Iniciando criação de pedido para userId: {}, request: {}", userId, request);

        try {
            // 1. Verificar consistência entre userId do path e do request
            if (!Objects.equals(userId, request.userId())) {
                logger.warn("UserId inconsistency: pathUserId={}, requestUserId={}", userId, request.userId());
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "User ID inconsistency");
            }

            // 2. Obter autenticação do contexto de segurança
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.warn("Unauthenticated user for userId: {}", userId);
                return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthenticated user");
            }

            // 3. Verificar se o usuário autenticado corresponde ao userId
            String username = authentication.getName(); // Email do usuário autenticado
            UUID authenticatedUserId = jwtUtil.getUserIdFromUsername(username);
            if (!Objects.equals(authenticatedUserId, userId)) {
                logger.warn("Unauthorized access: authenticatedUserId={}, pathUserId={}", authenticatedUserId, userId);
                return buildErrorResponse(HttpStatus.FORBIDDEN, "Unauthorized access");
            }

            // 4. Criar pedido
            OrderItemResponse response = orderItemService.createOrderItem(userId, request);
            logger.info("Request created successfully for userId: {}", userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response));

        } catch (UsernameNotFoundException e) {
            logger.error("Usuário não encontrado: {}", e.getMessage());
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "User not found: " + e.getMessage());
        } catch (OrderCreationFailedException e) {
            logger.error("Order creation failed: {}", e.getMessage());
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Order creation failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error creating order: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        ApiErrorResponse error = ApiErrorResponse.create(status, message);
        return ResponseEntity.status(status)
                .body(ApiResponse.error(error));
    }
}