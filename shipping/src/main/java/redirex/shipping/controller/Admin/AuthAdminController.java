package redirex.shipping.controller.Admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
public class AuthAdminController {

    private final AuthenticationManager authenticationManager;
    private final AdminServiceImpl adminService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthAdminResponse>> login(@Valid @RequestBody AuthAdminRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            UUID adminId = adminService.findAdminIdByEmail(authRequest.getEmail());
            String token = jwtUtil.generateToken(authRequest.getEmail(), adminId);

            AuthAdminResponse data = AuthAdminResponse.builder().token(token).fullname(authRequest.getFullname()).email(authRequest.getEmail()).build();

            return ResponseEntity.ok(ApiResponse.<AuthAdminResponse>builder().data(data).build());

        } catch (BadCredentialsException e) {
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid email or password")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<AuthAdminResponse>builder().error(error).build());
        }
    }
}