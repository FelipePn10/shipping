package redirex.shipping.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyCodeRequest (
    @Email
    @NotBlank
    String email,

    @NotBlank
    String code
    ) {

}