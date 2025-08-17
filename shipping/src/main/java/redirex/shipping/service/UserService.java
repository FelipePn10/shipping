package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.dto.response.UserResponse;
import redirex.shipping.dto.RegisterUserDTO;

import java.util.UUID;

public interface UserService {
    UserResponse registerUser(@Valid RegisterUserDTO dto);
    UserResponse updateUserProfile(UUID id, @Valid RegisterUserDTO dto);

    UUID findUserIdByEmail(String email);
    UserResponse findUserById(UUID id);
}