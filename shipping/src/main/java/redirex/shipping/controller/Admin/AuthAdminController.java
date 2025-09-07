package redirex.shipping.controller.Admin;

import jakarta.validation.Valid;
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
import redirex.shipping.dto.response.ApiErrorResponse;
import redirex.shipping.dto.response.ApiResponse;
import redirex.shipping.dto.response.AuthAdminResponse;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.admin.AdminServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AuthAdminController {
    private static final Logger log = LoggerFactory.getLogger(AuthAdminController.class);

    private final AuthenticationManager authenticationManager;
    private final AdminServiceImpl adminService;
    private final JwtUtil jwtUtil;

    public AuthAdminController(
            AuthenticationManager authenticationManager,
            AdminServiceImpl adminService,
            JwtUtil jwtUtil
    ) {
        this.authenticationManager = authenticationManager;
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthAdminResponse>> login(@Valid @RequestBody AuthAdminRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password())
            );

            UUID adminId = adminService.findAdminIdByEmail(authRequest.email());
            String token = jwtUtil.generateToken(authRequest.email(), adminId);

            AuthAdminResponse data = new AuthAdminResponse(authRequest.fullname(), authRequest.email(), token);

            return ResponseEntity.ok(ApiResponse.success(data));

        } catch (BadCredentialsException e) {
            ApiErrorResponse error = ApiErrorResponse.create(HttpStatus.UNAUTHORIZED, "Invalid email or password");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(error));
        }
    }
}