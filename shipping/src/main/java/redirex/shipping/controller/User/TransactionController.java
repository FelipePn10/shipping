package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.DepositRequestDto;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.dto.response.WalletTransactionResponse;
import redirex.shipping.service.OrderItemService;
import redirex.shipping.service.UserWalletServiceImpl;

import java.util.UUID;

@RestController
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final UserWalletServiceImpl userWalletService;
    private final OrderItemService orderItemService;

    public TransactionController(UserWalletServiceImpl userWalletService, OrderItemService orderItemService) {
        this.userWalletService = userWalletService;
        this.orderItemService = orderItemService;
    }

    @PostMapping("private/v1/api/users/{userId}/deposit")
    public ResponseEntity<WalletTransactionResponse> depositToWallet(
            @PathVariable UUID userId,
            @Valid @RequestBody DepositRequestDto depositRequestDto) {

        logger.info("Processing deposit request for userId: {}", userId);
        WalletTransactionResponse response = userWalletService.depositToWallet(userId, depositRequestDto);

        if ("error".equals(response.status())) {
            logger.error("Deposit failed for userId: {}. Reason: {}", userId, response.errorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        logger.info("Deposit successful for userId: {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("private/v1/api/users/{userId}/orders/{orderId}/payment")
    public ResponseEntity<?> processPayment(
            @PathVariable UUID userId,
            @PathVariable UUID orderId) {

        try {
            logger.info("Received request to process payment for order ID: {}", orderId);
            OrderItemResponse response = orderItemService.processOrderPayment(orderId, userId);
            logger.info("Payment processed successfully for order ID: {}", orderId);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            logger.error("Payment processing failed for order ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Payment processing failed: " + e.getMessage());
        }
    }
}