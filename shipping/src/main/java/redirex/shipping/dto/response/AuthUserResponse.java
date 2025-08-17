package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AuthUserResponse {
    private String token;
    private UUID userId;
}