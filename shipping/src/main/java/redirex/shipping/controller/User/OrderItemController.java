package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.exception.OrderCreationFailedException;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.OrderItemService;

import java.util.Objects;

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
            Long authenticatedUserId = jwtUtil.getUserIdFromUsername(username);
            if (!Objects.equals(authenticatedUserId, userId)) {
                logger.warn("Unauthorized access: authenticatedUserId={}, pathUserId={}", authenticatedUserId, userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null);
            }

            // 4. Criar pedido
            OrderItemResponse response = orderItemService.createOrderItem(userId, request);
            logger.info("Request created successfully for userId: {}", userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (UsernameNotFoundException e) {
            logger.error("Usuário não encontrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (OrderCreationFailedException e) {
            logger.error("Falha na criação do pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar pedido: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}