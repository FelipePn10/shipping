package redirex.shipping.dto.response;

import java.util.UUID;

public record AuthUserResponse (
    String token,
    UUID userId
) {

}