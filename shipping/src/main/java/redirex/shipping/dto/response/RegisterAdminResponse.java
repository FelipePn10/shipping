package redirex.shipping.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterAdminResponse {
    private String fullname;
    private String password;
    private String email;
    private String cpf;
    private String role;
    private LocalDateTime createdAt;
}
