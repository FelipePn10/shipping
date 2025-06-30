package redirex.shipping.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthUserResponse {
    private String token;
    private Long userId;
}