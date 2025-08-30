package redirex.shipping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserUpdateResponse {
    private String fullname;
    private String email;
    private String password;
    private String cpf;
    private String phone;
    private String occupation;
    private LocalDateTime updatedAt;
}
