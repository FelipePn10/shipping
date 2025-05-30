package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.dto.response.UserResponse;
import redirex.shipping.dto.RegisterUserDTO;

public interface UserService {
    UserResponse registerUser(@Valid RegisterUserDTO dto);
    UserResponse updateUserProfile(Long id, @Valid RegisterUserDTO dto);

    Long findUserIdByEmail(String email);
    UserResponse findUserById(Long id);
}