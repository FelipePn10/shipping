package redirex.shipping.mapper;

import org.springframework.stereotype.Component;
import redirex.shipping.dto.internal.UserInternalResponse;
import redirex.shipping.dto.response.UserRegisterResponse;
import redirex.shipping.dto.response.UserUpdateResponse;
import redirex.shipping.entity.UserEntity;

@Component
public class UserMapper {

    public UserRegisterResponse toResponseRegisterUser(UserEntity user) {
        if (user == null) {
            return null;
        }
        return UserRegisterResponse.builder()
                .fullname(user.getFullname())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UserUpdateResponse toResponseUpdateUser(UserEntity user) {
        if (user == null) {
            return null;
        }
        return UserUpdateResponse.builder()
                .fullname(user.getFullname())
                .email(user.getEmail())
                .cpf(user.getCpf())
                .phone(user.getPhone())
                .occupation(user.getOccupation())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserInternalResponse toResponseUser(UserEntity user) {
        if (user == null) {
            return null;
        }
        return UserInternalResponse.builder()
                .fullname(user.getFullname())
                .email(user.getEmail())
                .cpf(user.getCpf())
                .phone(user.getPhone())
                .occupation(user.getOccupation())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                .build();
    }
}
