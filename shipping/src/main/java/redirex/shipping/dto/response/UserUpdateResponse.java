package redirex.shipping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateResponse {
    private String fullname;
    private String email;
    private String password;
    private String cpf;
    private String phone;
    private String occupation;
}
