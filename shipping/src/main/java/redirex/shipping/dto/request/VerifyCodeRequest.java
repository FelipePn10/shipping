package redirex.shipping.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String code;
}