package redirex.shipping.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserInternalResponse {
    private UUID id;
    private String fullname;
    private String email;
    private String cpf;
    private String phone;
    private String occupation;
    private String createdAt;
    private String updatedAt;
}
