package redirex.shipping.dto.response;

public record AuthAdminResponse (
    String fullname,
    String email,
    String token
) {

}
