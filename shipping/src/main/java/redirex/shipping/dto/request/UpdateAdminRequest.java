package redirex.shipping.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAdminRequest {
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullname;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
