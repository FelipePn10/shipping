package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.internal.UserInternalResponse;
import redirex.shipping.dto.request.UpdateUserRequest;
import redirex.shipping.dto.response.*;
import redirex.shipping.dto.request.RegisterUserRequest;
import redirex.shipping.exception.UnauthorizedAccessException;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.UserServiceImpl;

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
            logger.info("Received request to register user: {}", registerUserRequest.email());
            UserRegisterResponse userRegisterResponse = userService.registerUser(registerUserRequest);
            logger.info("User registered successfully: {}", userRegisterResponse.email());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(userRegisterResponse));

        } catch (Exception e) {
            logger.error("Registration error: {}", e.getMessage(), e);
            ApiErrorResponse error = ApiErrorResponse.create(HttpStatus.BAD_REQUEST,
                    "Error registering user. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(error));
        }
    }

    @GetMapping("/api/user/{id}")
    public ResponseEntity<ApiResponse<UserInternalResponse>> getUserById(@PathVariable UUID id) {
        try {
            validateUserAccess(id);
            logger.info("Received request to get user by ID: {}", id);
            UserInternalResponse userInternalResponse = userService.findUserById(id);

            return ResponseEntity.ok(ApiResponse.success(userInternalResponse));

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
                    .body(ApiResponse.success(userUpdateResponse));

        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access attempt for user {}: {}", id, e.getMessage());
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating user profile for user ID {}: {}", id, e.getMessage(), e);
            ApiErrorResponse error = ApiErrorResponse.create(HttpStatus.BAD_REQUEST,
                    "Error update user. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(error));
        }
    }

    private void validateUserAccess(UUID requestedId) {
        String token = ((String) SecurityContextHolder.getContext().getAuthentication().getCredentials()).substring(7); // Remove "Bearer "
        UUID userIdFromToken = jwtUtil.getUserIdFromToken(token);
        if (!userIdFromToken.equals(requestedId)) {
            throw new UnauthorizedAccessException("You are not authorized to access this user's data");
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        ApiErrorResponse error = ApiErrorResponse.create(status, message);
        return ResponseEntity.status(status)
                .body(ApiResponse.error(error));
    }
}