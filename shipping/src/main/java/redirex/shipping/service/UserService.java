package redirex.shipping.service;

import redirex.shipping.controller.dto.response.UserResponse;
import redirex.shipping.dto.RegisterUserDTO;

public interface UserService {
    UserResponse registerUser(RegisterUserDTO dto);
    UserResponse findUserById(Long id);
    UserResponse updateUserProfile(Long id, RegisterUserDTO dto);
}