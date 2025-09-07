package redirex.shipping.dto.internal;


import java.util.UUID;

public record UserInternalResponse(
        UUID id,
        String fullname,
        String email,
        String cpf,
        String phone,
        String occupation,
        String createdAt,
        String updatedAt
) {}
