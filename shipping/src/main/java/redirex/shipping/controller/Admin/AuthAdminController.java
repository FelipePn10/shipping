package redirex.shipping.controller.Admin;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redirex.shipping.dto.request.AuthAdminRequest;
import redirex.shipping.dto.response.AuthAdminResponse;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.admin.AdminServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("/public/auth/admin/v1")
@RequiredArgsConstructor
public class AuthAdminController {
    private static final Logger logger = LoggerFactory.getLogger(AuthAdminController.class);

    private final AuthenticationManager authenticationManager;
    private final AdminServiceImpl adminService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthAdminRequest authRequest) {
        logger.info("Login attempt for email: {}", authRequest.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
            UUID adminId = adminService.findAdminIdByEmail(authRequest.getEmail());
            if(adminId == null) {
                logger.error("Admin ID not found for email: {}", authRequest.getEmail());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin data not found");
            }
            String token = jwtUtil.generateToken(authRequest.getEmail(), adminId);
            AuthAdminResponse authAdminResponse = AuthAdminResponse.builder()
                    .token(token)
                    .build();
            return ResponseEntity.ok(authAdminResponse);
        } catch (BadCredentialsException e) {
            logger.error("Invalid email or password: {}", authRequest.getEmail());
            return ResponseEntity.badRequest().body("Invalid email or password");
        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Authentication failed");
        }
    }
}
