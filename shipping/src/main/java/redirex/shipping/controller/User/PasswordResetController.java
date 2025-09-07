package redirex.shipping.controller.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.ForgotPasswordRequest;
import redirex.shipping.dto.request.ResetPasswordRequest;
import redirex.shipping.dto.request.VerifyCodeRequest;
import redirex.shipping.dto.response.ApiErrorResponse;
import redirex.shipping.dto.response.ApiResponse;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.service.UserPasswordResetService;
import redirex.shipping.service.email.UserEmailService;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class PasswordResetController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    private final UserPasswordResetService passwordResetService;
    private final UserEmailService userEmailService;

    public PasswordResetController(UserPasswordResetService passwordResetService, UserEmailService userEmailService) {
        this.passwordResetService = passwordResetService;
        this.userEmailService = userEmailService;
    }

    @PostMapping("/public/user/account/change/password/redirex")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        logger.info("Password reset requested for email {}", request.email());

        try {
            Optional<UserEntity> userOptional = passwordResetService.findUserByEmail(request.email());

            if (userOptional.isEmpty()) {
                logger.warn("User not found for email: {}", request.email());
                return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found");
            }

            UserEntity user = userOptional.get();
            String code = passwordResetService.createResetCode(user);
            userEmailService.sendPasswordResetCodeEmail(user.getEmail(), code);

            return ResponseEntity.ok(ApiResponse.success("Verification code sent to email"));

        } catch (Exception e) {
            logger.error("Error processing password reset request: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

    @PostMapping("/public/user/account/verify/reset/code/redirex")
    public ResponseEntity<ApiResponse<String>> verifyResetCode(@Valid @RequestBody VerifyCodeRequest request) {
        try {
            Optional<UserEntity> userOptional = passwordResetService.findUserByEmail(request.email());

            if (userOptional.isEmpty()) {
                logger.warn("User not found for email: {}", request.email());
                return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found");
            }

            UserEntity user = userOptional.get();
            String sessionToken = passwordResetService.verifyCode(user, request.code());

            return ResponseEntity.ok(ApiResponse.success(sessionToken));

        } catch (Exception e) {
            logger.error("Error verifying reset code: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid verification code");
        }
    }

    @PostMapping("/public/user/account/reset/password/redirex")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.resetSessionToken(), request.newPassword());
            return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));

        } catch (Exception e) {
            logger.error("Error resetting password: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Error resetting password");
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        ApiErrorResponse error = ApiErrorResponse.create(status, message);
        return ResponseEntity.status(status)
                .body(ApiResponse.error(error));
    }
}