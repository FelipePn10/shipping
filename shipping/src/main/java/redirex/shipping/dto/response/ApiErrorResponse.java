package redirex.shipping.dto.response;

public record ApiErrorResponse (
    String message,
    int status
) {

}
