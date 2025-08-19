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

import java.util.Map;
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
        logger.info("Tentativa de login para email: {}", authRequest.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            UUID adminId = adminService.findAdminIdByEmail(authRequest.getEmail());
            if (adminId == null) {
                logger.error("ID do admin não encontrado para email: {}", authRequest.getEmail());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Dados do admin não encontrados"));
            }

            String token = jwtUtil.generateToken(authRequest.getEmail(), adminId);
            return ResponseEntity.ok(
                    AuthAdminResponse.builder()
                            .token(token)
                            .build()
            );
        } catch (BadCredentialsException e) {
            logger.warn("Tentativa de login inválida para email: {}", authRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email ou senha inválidos"));
        } catch (Exception e) {
            logger.error("Falha na autenticação: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha na autenticação"));
        }
    }
}