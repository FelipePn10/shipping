package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.AddressResponse;
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
@RequiredArgsConstructor
public class OrderItemController {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemController.class);

    private final OrderItemService orderItemService;
    private final JwtUtil jwtUtil;

    @PostMapping("/{userId}/create/order")
    public ResponseEntity<ApiResponse<OrderItemResponse>> createOrder(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateOrderItemRequest request) {

        logger.info("Iniciando criação de pedido para userId: {}, request: {}", userId, request);

        try {
            // 1. Verificar consistência entre userId do path e do request
            if (!Objects.equals(userId, request.getUserId())) {
                logger.warn("UserId inconsistency: pathUserId={}, requestUserId={}", userId, request.getUserId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }

            // 2. Obter autenticação do contexto de segurança
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.warn("Unauthenticated user for userId: {}", userId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(null);
            }

            // 3. Verificar se o usuário autenticado corresponde ao userId
            String username = authentication.getName(); // Email do usuário autenticado
            UUID authenticatedUserId = jwtUtil.getUserIdFromUsername(username);
            if (!Objects.equals(authenticatedUserId, userId)) {
                logger.warn("Unauthorized access: authenticatedUserId={}, pathUserId={}", authenticatedUserId, userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null);
            }

            // 4. Criar pedido
            OrderItemResponse response = orderItemService.createOrderItem(userId, request);
            logger.info("Request created successfully for userId: {}", userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<OrderItemResponse>builder()
                            .data(response)
                            .timestamp(LocalDateTime.now())
                            .build());
        } catch (UsernameNotFoundException e) {
            logger.error("Usuário não encontrado: {}", e.getMessage());
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Address not found. Reason: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<OrderItemResponse>builder().error(error).build());
        } catch (OrderCreationFailedException e) {
            logger.error("Order creation failed: {}", e.getMessage());
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Address not found. Reason: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<OrderItemResponse>builder().error(error).build());
        } catch (Exception e) {
            logger.error("Unexpected error creating order: {}", e.getMessage(), e);
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Internal server error. Reason: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<OrderItemResponse>builder().error(error).build());
        }
    }
}