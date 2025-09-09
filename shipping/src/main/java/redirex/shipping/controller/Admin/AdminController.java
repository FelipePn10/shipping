package redirex.shipping.controller.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.response.ApiErrorResponse;
import redirex.shipping.dto.response.ApiResponse;
import redirex.shipping.dto.response.DeleteUserResponse;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.service.UserService;
import java.util.UUID;

@RestController
@Slf4j
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping("api/admin/v1/delete/user/{userId}")
    public ResponseEntity<ApiResponse<DeleteUserResponse>> deleteUserProfile(@PathVariable UUID userId) {
        try {
            log.info("Deleting address for zipcode: {}", userId);
            userService.deleteUserProfile(userId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            log.warn("Address not found: {}", userId);
            return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Delete error for userId {}: {}", userId, e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        ApiErrorResponse error = ApiErrorResponse.create(status, message);
        return ResponseEntity.status(status)
                .body(ApiResponse.error(error));
    }
}

