package redirex.shipping.controller.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import redirex.shipping.dto.request.ForgotPasswordRequest;
import redirex.shipping.dto.request.ResetPasswordRequest;
import redirex.shipping.dto.request.VerifyCodeRequest;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.service.UserPasswordResetService;

import jakarta.validation.Valid;
import redirex.shipping.service.email.UserEmailService;

import java.util.Map;

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
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        logger.info("Password reset requested for email {}", request.getEmail());

        UserEntity user = passwordResetService.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String code = passwordResetService.createResetCode(user);
        userEmailService.sendPasswordResetCodeEmail(user.getEmail(), code);

        return ResponseEntity.ok(Map.of("message", "Verification code sent to email"));
    }

    @PostMapping("/public/user/account/verify/reset/code/redirex")
    public ResponseEntity<?> verifyResetCode(@Valid @RequestBody VerifyCodeRequest request) {
        UserEntity user = passwordResetService.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String sessionToken = passwordResetService.verifyCode(user, request.getCode());
        return ResponseEntity.ok(Map.of("resetSessionToken", sessionToken));
    }

    @PostMapping("/public/user/account/reset/password/redirex")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getResetSessionToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }
}