package redirex.shipping.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import redirex.shipping.util.CpfMaskSerializer;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserRegisterResponse {
    private String fullname;
    private String email;
    @JsonSerialize(using = CpfMaskSerializer.class)
    private String cpf;
    private String phone;
    private String occupation;
    private LocalDateTime createdAt;
}
