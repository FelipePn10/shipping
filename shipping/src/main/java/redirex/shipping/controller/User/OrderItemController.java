package redirex.shipping.controller.User;

import jakarta.servlet.http.HttpServletRequest;
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

        try{
            // 1. Verificamos a consistência de IDs no path e body
            if(!userId.equals(request.getUserId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            // 2. Obter autenticação do contexto de segurança
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 3. Verifica se o usuário autenticado é o mesmo do path
            if (!authentication.getName().equals(String.valueOf(userId))){
                logger.warn("ID mismatch: authUserId={}, pathUserId={}", authentication.getName(), userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // 4. Criar pedido
            OrderItemResponse response = orderItemService.createOrderItem(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UsernameNotFoundException e) {
            logger.error("Username not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (OrderCreationFailedException e) {
            logger.error("Order creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e){
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}