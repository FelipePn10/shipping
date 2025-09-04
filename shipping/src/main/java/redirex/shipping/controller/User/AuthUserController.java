package redirex.shipping.controller.User;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.AuthUserRequest;
import redirex.shipping.dto.response.ApiErrorResponse;
import redirex.shipping.dto.response.ApiResponse;
import redirex.shipping.dto.response.AuthUserResponse;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.TokenBlacklistService;
import redirex.shipping.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/public/auth/v1")
@RequiredArgsConstructor
public class AuthUserController {
    private static final Logger logger = LoggerFactory.getLogger(AuthUserController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserServiceImpl userService;

    @PostMapping("/user/login")
    public ResponseEntity<ApiResponse<AuthUserResponse>> login(
            @Valid @RequestBody AuthUserRequest authRequest) {
        logger.info("Login attempt for email: {}", authRequest.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
            UUID userId = userService.findUserIdByEmail(authRequest.getEmail());
            String token = jwtUtil.generateToken(authRequest.getEmail(), userId);

            AuthUserResponse response = AuthUserResponse.builder()
                    .token(token)
                    .userId(userId)
                    .build();

            logger.info("Successful login for email: {}", authRequest.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<AuthUserResponse>builder()
                            .data(response)
                            .timestamp(LocalDateTime.now())
                            .build());

        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for email: {}", authRequest.getEmail());
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid credentials");
        } catch (Exception e) {
            logger.error("Error processing login: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing login");
        }
    }

    @PostMapping("/user/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        final String BEARER_PREFIX = "Bearer ";
        logger.info("Logout request received");

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("Invalid Authorization header format");
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid Authorization header");
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        try {
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                logger.warn("Token is already blacklisted");
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "Token already invalidated");
            }

            long expirationInSeconds = jwtUtil.getExpirationTimeInSeconds(token);
            if (expirationInSeconds <= 0) {
                logger.warn("Token has already expired");
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "Token already expired");
            }

            tokenBlacklistService.addToBlacklist(token, expirationInSeconds);
            logger.info("Token successfully blacklisted. Expires in {} seconds", expirationInSeconds);

            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .data("Logout successful")
                    .timestamp(LocalDateTime.now())
                    .build());

        } catch (TokenBlacklistService.RedisOperationException e) {
            logger.error("Redis communication failure: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable");
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(status.value())
                .message(message)
                .build();

        ApiResponse<T> response = ApiResponse.<T>builder()
                .data(null)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(response);
    }
}