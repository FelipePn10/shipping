package redirex.shipping.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import redirex.shipping.util.CpfMaskSerializer;

import java.time.LocalDateTime;

@Data
public class RegisterAdminResponse {
    private String fullname;
    private String email;
    @JsonSerialize(using = CpfMaskSerializer.class)
    private String cpf;
    private String role;
    private LocalDateTime createdAt;
}
