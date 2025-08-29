package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.UpdateUserRequest;
import redirex.shipping.dto.response.*;
import redirex.shipping.dto.request.ForgotPasswordRequest;
import redirex.shipping.dto.request.RegisterUserRequest;
import redirex.shipping.dto.request.ResetPasswordRequest;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.exception.*;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.UserPasswordResetService;
import redirex.shipping.service.UserServiceImpl;
import redirex.shipping.service.email.UserEmailService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserServiceImpl userService;
    private final UserEmailService emailService;
    private final UserPasswordResetService passwordResetService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/public/auth/v1/user/register")
    public ResponseEntity<ApiResponse<UserRegisterResponse>> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        try {
            logger.info("Received request to register user: {}", registerUserRequest.getEmail());
            UserRegisterResponse userRegisterResponseResponse = userService.registerUser(registerUserRequest);
            logger.info("User registered successfully: {}", userRegisterResponseResponse.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<UserRegisterResponse>builder()
                            .data(userRegisterResponseResponse)
                            .timestamp(LocalDateTime.now())
                            .build());
        } catch (UserRegistrationException e) {
            logger.error("Registration error: {}", e.getMessage(), e);
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Error registering user. Reason: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<UserRegisterResponse>builder().error(error).build());
        }
    }

    @PostMapping("/public/user/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        logger.info("Password reset request for email: {}", forgotPasswordRequest.getEmail());
        Optional<UserEntity> userOptional = passwordResetService.findUserByEmail(forgotPasswordRequest.getEmail());
        if (userOptional.isEmpty()) {
            logger.warn("No user found with email: {}", forgotPasswordRequest.getEmail());
            return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found");
        }

        UserEntity user = userOptional.get();
        passwordResetService.generateResetToken(user);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getPasswordResetToken());
        return ResponseEntity.ok(buildSuccessResponse("Password reset email sent successfully"));
    }

    @PostMapping("/public/user/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        logger.info("Password reset attempt for email: {}", resetPasswordRequest.getEmail());
        Optional<UserEntity> userOptional = passwordResetService.findUserByEmail(resetPasswordRequest.getEmail());
        if (userOptional.isEmpty()) {
            logger.warn("No user found with email: {}", resetPasswordRequest.getEmail());
            return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found");
        }

        UserEntity user = userOptional.get();
        String resetToken = user.getPasswordResetToken();
        LocalDateTime tokenExpiry = user.getPasswordResetTokenExpiry();

        if (resetToken == null || tokenExpiry == null || !resetToken.equals(resetPasswordRequest.getToken())) {
            logger.warn("Invalid or missing reset token for email: {}", resetPasswordRequest.getEmail());
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid reset token");
        }

        if (tokenExpiry.isBefore(LocalDateTime.now())) {
            logger.warn("Expired token for email: {}", resetPasswordRequest.getEmail());
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Reset token expired");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        passwordResetService.saveUser(user);
        return ResponseEntity.ok(buildSuccessResponse("Password reset successfully"));
    }

    @GetMapping("/api/user/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        try {
            validateUserAccess(id);
            logger.info("Received request to get user by ID: {}", id);
            UserResponse userResponse = userService.findUserById(id);
            return ResponseEntity.ok(userResponse);
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access attempt for user {}: {}", id, e.getMessage());
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            logger.error("Error retrieving user with ID {}: {}", id, e.getMessage(), e);
            return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @PutMapping("/api/user/{id}/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUserProfile(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        try {
            validateUserAccess(id);
            logger.info("Received request to update profile for user ID: {}", id);
            UserUpdateResponse userUpdateResponse = userService.updateUserProfile(id, updateUserRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<UserUpdateResponse>builder()
                            .data(userUpdateResponse)
                            .timestamp(LocalDateTime.now())
                            .build());
        } catch (Exception e) {
            logger.error("Error updating user profile for user ID {}: {}", id, e.getMessage(), e);
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Error update user. Reason: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<UserUpdateResponse>builder().error(error).build());
        }
    }

    private void validateUserAccess(UUID requestedId) {
        String token = ((String) SecurityContextHolder.getContext().getAuthentication().getCredentials()).substring(7); // Remove "Bearer "
        UUID userIdFromToken = jwtUtil.getUserIdFromToken(token);
        if (!userIdFromToken.equals(requestedId)) {
            throw new UnauthorizedAccessException("You are not authorized to access this user's data");
        }
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    private Map<String, Object> buildSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("message", message);
        return response;
    }
}