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
import redirex.shipping.dto.response.AuthUserResponse;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.TokenBlacklistService;
import redirex.shipping.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public/auth/v1")
@RequiredArgsConstructor
public class AuthUserController {
    private static final Logger logger = LoggerFactory.getLogger(AuthUserController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserServiceImpl userService;

    @PostMapping("user/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthUserRequest authRequest) {
        logger.info("Login attempt for email: {}", authRequest.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
            Long userId = userService.findUserIdByEmail(authRequest.getEmail());
            String token = jwtUtil.generateToken(authRequest.getEmail(), userId);
            AuthUserResponse response = AuthUserResponse.builder()
                    .token(token)
                    .build();
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for email: {}", authRequest.getEmail());
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        } catch (Exception e) {
            logger.error("Error processing login: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing login");
        }
    }

    @PostMapping("/login/enterprise")
    public ResponseEntity<?> loginEnterprise(@Valid @RequestBody AuthUserRequest authRequest) {
        logger.info("Enterprise login attempt for email: {}", authRequest.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
            Long userId = userService.findUserIdByEmail(authRequest.getEmail());
            String token = jwtUtil.generateToken(authRequest.getEmail(), userId);
            AuthUserResponse response = AuthUserResponse.builder()
                    .token(token)
                    .build();
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for email: {}", authRequest.getEmail());
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        } catch (Exception e) {
            logger.error("Error processing enterprise login: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing login");
        }
    }

    @PostMapping("user/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        final String BEARER_PREFIX = "Bearer ";
        logger.info("Logout request received");

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("Invalid Authorization header format");
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid Authorization header");
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        try {
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                logger.warn("Token already blacklisted");
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "Token already invalidated");
            }

            long expirationInSeconds = jwtUtil.getExpirationTimeInSeconds(token);
            if (expirationInSeconds <= 0) {
                logger.warn("Token already expired");
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "Token already expired");
            }

            tokenBlacklistService.addToBlacklist(token, expirationInSeconds);
            logger.info("Token successfully blacklisted. Expires in {} seconds", expirationInSeconds);
            return ResponseEntity.ok(buildSuccessResponse("Logout successful"));
        } catch (TokenBlacklistService.RedisOperationException e) {
            logger.error("Redis communication failure: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable");
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid token");
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