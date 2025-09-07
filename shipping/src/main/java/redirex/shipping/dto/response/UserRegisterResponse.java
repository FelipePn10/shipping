package redirex.shipping.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import redirex.shipping.util.CpfMaskSerializer;

import java.time.LocalDateTime;

public record UserRegisterResponse (
    String fullname,
    String email,
    @JsonSerialize(using = CpfMaskSerializer.class)
    String cpf,
    String phone,
    String occupation,
    LocalDateTime createdAt
) {

}
