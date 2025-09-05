package redirex.shipping.dto.request;

import jakarta.validation.constraints.NotBlank;


public record ResetPasswordRequest (
    @NotBlank
    String resetSessionToken,

    @NotBlank
    String newPassword
    ) {

}