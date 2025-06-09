package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import redirex.shipping.dto.request.DepositRequestDto;
import redirex.shipping.dto.response.WalletTransactionResponse;
import redirex.shipping.exception.DepositWalletExecption;
import redirex.shipping.exception.StripePaymentException;
import redirex.shipping.service.UserWalletServiceImpl;

@RestController
@RequiredArgsConstructor
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final UserWalletServiceImpl userWalletService;

    @PostMapping("private/v1/api/users/{userId}/deposit")
    public ResponseEntity<WalletTransactionResponse> depositToWallet(
            @PathVariable Long userId,
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
}