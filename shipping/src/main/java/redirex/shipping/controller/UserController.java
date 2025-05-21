package redirex.shipping.controller;

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
import redirex.shipping.controller.dto.request.CreateAddressRequest;
import redirex.shipping.controller.dto.response.AddressResponse;
import redirex.shipping.controller.dto.response.UserResponse;
import redirex.shipping.dto.AddressDTO;
import redirex.shipping.dto.ForgotPasswordDTO;
import redirex.shipping.dto.RegisterUserDTO;
import redirex.shipping.dto.ResetPasswordDTO;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.exception.UnauthorizedAccessException;
import redirex.shipping.exception.UserRegistrationException;
import redirex.shipping.mapper.AddressMapper;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.AddressService;
import redirex.shipping.service.UserPasswordResetService;
import redirex.shipping.service.UserServiceImpl;
import redirex.shipping.service.email.UserEmailService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserServiceImpl userService;
    private final UserEmailService emailService;
    private final UserPasswordResetService passwordResetService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AddressService addressService;
    private final AddressMapper addressMapper;

    @PostMapping("/public/user/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        try {
            logger.info("Received request to register user: {}", registerUserDTO.getEmail());
            UserResponse userResponse = userService.registerUser(registerUserDTO);
            logger.info("User registered successfully: {}", userResponse.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
        } catch (UserRegistrationException e) {
            logger.error("Registration error: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during registration: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error registering user");
        }
    }

    @PostMapping("/public/user/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        logger.info("Password reset request for email: {}", forgotPasswordDTO.getEmail());
        Optional<UserEntity> userOptional = passwordResetService.findUserByEmail(forgotPasswordDTO.getEmail());
        if (userOptional.isEmpty()) {
            logger.warn("No user found with email: {}", forgotPasswordDTO.getEmail());
            return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found");
        }

        UserEntity user = userOptional.get();
        passwordResetService.generateResetToken(user);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getPasswordResetToken());
        return ResponseEntity.ok(buildSuccessResponse("Password reset email sent successfully"));
    }

    @PostMapping("/public/user/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        logger.info("Password reset attempt for email: {}", resetPasswordDTO.getEmail());
        Optional<UserEntity> userOptional = passwordResetService.findUserByEmail(resetPasswordDTO.getEmail());
        if (userOptional.isEmpty()) {
            logger.warn("No user found with email: {}", resetPasswordDTO.getEmail());
            return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found");
        }

        UserEntity user = userOptional.get();
        String resetToken = user.getPasswordResetToken();
        LocalDateTime tokenExpiry = user.getPasswordResetTokenExpiry();

        if (resetToken == null || tokenExpiry == null || !resetToken.equals(resetPasswordDTO.getToken())) {
            logger.warn("Invalid or missing reset token for email: {}", resetPasswordDTO.getEmail());
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid reset token");
        }

        if (tokenExpiry.isBefore(LocalDateTime.now())) {
            logger.warn("Expired token for email: {}", resetPasswordDTO.getEmail());
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Reset token expired");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        passwordResetService.saveUser(user);
        return ResponseEntity.ok(buildSuccessResponse("Password reset successfully"));
    }

    @PostMapping("/public/user/created-address")
    public ResponseEntity<?> createAddress(@Valid @RequestBody CreateAddressRequest request) {
        try {
            AddressDTO dto = addressMapper.toDTO(request);
            AddressResponse response = addressService.createdAddress(dto);
            return ResponseEntity.created(URI.create("/" + response.getId())).body(response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Criar o metodo para dar fazer update no endereço

    @GetMapping("/api/user/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            validateUserAccess(id);
            logger.info("Received request to get user by ID: {}", id);
            UserResponse userResponse = userService.findUserById(id);
            return ResponseEntity.ok(userResponse);
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access attempt for user ID {}: {}", id, e.getMessage());
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            logger.error("Error retrieving user with ID {}: {}", id, e.getMessage(), e);
            return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @PutMapping("/api/user/{id}/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long id, @Valid @RequestBody RegisterUserDTO registerUserDTO) {
        try {
            validateUserAccess(id);
            logger.info("Received request to update profile for user ID: {}", id);
            UserResponse userResponse = userService.updateUserProfile(id, registerUserDTO);
            return ResponseEntity.ok(userResponse);
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access attempt for user ID {}: {}", id, e.getMessage());
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating user profile for ID {}: {}", id, e.getMessage(), e);
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Error updating user profile");
        }
    }

    private void validateUserAccess(Long requestedId) {
        String token = ((String) SecurityContextHolder.getContext().getAuthentication().getCredentials()).substring(7); // Remove "Bearer "
        Long userIdFromToken = jwtUtil.getUserIdFromToken(token);
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