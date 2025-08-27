package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.dto.response.UserResponse;
import redirex.shipping.dto.request.RegisterUserRequest;

import java.util.UUID;

public interface UserService {
    UserResponse registerUser(@Valid RegisterUserRequest dto);
    UserResponse updateUserProfile(UUID id, @Valid RegisterUserRequest dto);

    UUID findUserIdByEmail(String email);
    UserResponse findUserById(UUID id);
}