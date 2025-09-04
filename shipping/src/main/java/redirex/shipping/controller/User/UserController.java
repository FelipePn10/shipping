package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.internal.UserInternalResponse;
import redirex.shipping.dto.request.UpdateUserRequest;
import redirex.shipping.dto.response.*;
import redirex.shipping.dto.request.RegisterUserRequest;
import redirex.shipping.exception.*;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserServiceImpl userService;
    private final JwtUtil jwtUtil;

    public UserController(UserServiceImpl userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/public/auth/user/register")
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

    @GetMapping("/api/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        try {
            validateUserAccess(id);
            logger.info("Received request to get user by ID: {}", id);
            UserInternalResponse userInternalResponseResponse = userService.findUserById(id);
            return ResponseEntity.ok(userInternalResponseResponse);
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access attempt for user {}: {}", id, e.getMessage());
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            logger.error("Error retrieving user with ID {}: {}", id, e.getMessage(), e);
            return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @PutMapping("/api/v1/update/user/{id}/profile")
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUserProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
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