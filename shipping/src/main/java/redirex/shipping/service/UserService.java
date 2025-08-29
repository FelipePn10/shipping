package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.dto.request.UpdateUserRequest;
import redirex.shipping.dto.response.UserRegisterResponse;
import redirex.shipping.dto.response.UserResponse;
import redirex.shipping.dto.request.RegisterUserRequest;
import redirex.shipping.dto.response.UserUpdateResponse;

import java.util.UUID;

public interface UserService {
    UserRegisterResponse registerUser(@Valid RegisterUserRequest dto);
    UserUpdateResponse updateUserProfile(UUID userId, @Valid UpdateUserRequest dto);

    UUID findUserIdByEmail(String email);
    UserResponse findUserById(UUID userId);
}