package redirex.shipping.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import redirex.shipping.util.CpfMaskSerializer;

import java.time.LocalDateTime;

public record RegisterAdminResponse (
    String fullname,
    String email,
    @JsonSerialize(using = CpfMaskSerializer.class)
    String cpf,
    String role,
    LocalDateTime createdAt
) {

}
