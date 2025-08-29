package redirex.shipping.mapper;

import org.springframework.stereotype.Component;
import redirex.shipping.dto.response.UserRegisterResponse;
import redirex.shipping.dto.response.UserResponse;
import redirex.shipping.entity.UserEntity;

@Component
public class UserMapper {

    public UserRegisterResponse toResponse(UserEntity user) {
        if (user == null) {
            return null;
        }
        return UserRegisterResponse.builder()
                .id(user.getId())
                .name(user.getFullname())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}