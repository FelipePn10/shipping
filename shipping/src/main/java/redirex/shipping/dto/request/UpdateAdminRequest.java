package redirex.shipping.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateAdminRequest (
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    String fullname,

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,

    @Size(min = 8, message = "Password must be at least 8 characters")
    String password
) {

        }
