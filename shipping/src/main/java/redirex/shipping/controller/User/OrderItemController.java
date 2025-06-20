package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.exception.OrderCreationFailedException;
import redirex.shipping.service.OrderItemService;

@RestController
@RequiredArgsConstructor
public class OrderItemController {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemController.class);

    private OrderItemService orderItemService;

    @PostMapping("private/v1/api/users/{userId}/create/order")
    public ResponseEntity<OrderItemResponse> createOrder(
            @PathVariable Long userId,
            @Valid CreateOrderItemRequest request) {
        try {
            logger.info("Creating the order: {}", request);
            OrderItemResponse response = orderItemService.createOrderItem(userId, request);
            logger.info("Order created successfully. OrderId: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (OrderCreationFailedException ex) {
            logger.error("Order creation failed", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
