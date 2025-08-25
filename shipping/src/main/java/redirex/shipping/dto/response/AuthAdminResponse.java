package redirex.shipping.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthAdminResponse {
    private String fullname;
    private String email;
    private String token;
}
