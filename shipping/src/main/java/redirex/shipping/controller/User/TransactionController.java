package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.DepositRequestDto;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.dto.response.WalletTransactionResponse;
import redirex.shipping.exception.*;
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
        try {
            logger.info("Processing deposit request for userId: {}", userId);
            WalletTransactionResponse walletTransactionResponse = userWalletService.depositToWallet(userId, depositRequestDto);
            logger.info("Deposit successful for userId: {}", userId);
            return ResponseEntity.status(HttpStatus.OK).body(walletTransactionResponse);

        } catch (DepositWalletExecption e) {
            logger.error("Deposit failed for userId: {}. Reason: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new WalletTransactionResponse("error", e.getMessage()));

        } catch (StripePaymentException e) {
            logger.error("Stripe payment failed for userId: {}. Reason: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                    .body(new WalletTransactionResponse("error", "Payment processing failed: " + e.getMessage()));

        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for userId: {}. Reason: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new WalletTransactionResponse("error", e.getMessage()));

        } catch (Exception e) {
            logger.error("Unexpected error during deposit for userId: {}. Reason: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WalletTransactionResponse("error", "An unexpected error occurred."));
        }
    }

    @PostMapping("private/v1/api/users/{userId}/orders/{orderId}/payment")
    public ResponseEntity<OrderItemResponse> processPayment(
            @PathVariable UUID userId,
            @PathVariable UUID orderId) {

            logger.info("Received request to process payment for order ID: {}", orderId);
        OrderItemResponse response = orderItemService.processOrderPayment(orderId,userId);
            logger.info("Payment processed successfully for order ID: {}", orderId);
            return ResponseEntity.status(HttpStatus.OK).body(response);

    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Invalid request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data");
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        logger.error("Order state error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Invalid order state");
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        logger.error("Insufficient balance: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Insufficient balance");
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<String> handlePaymentProcessingException(PaymentProcessingException ex) {
        logger.error("Payment failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment error");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
    }
}