package redirex.shipping.mapper;

import org.springframework.stereotype.Component;
import redirex.shipping.dto.internal.UserInternalResponse;
import redirex.shipping.dto.response.UserRegisterResponse;
import redirex.shipping.dto.response.UserUpdateResponse;
import redirex.shipping.entity.UserEntity;

import java.util.UUID;

@Component
public class UserMapper {

    public UserRegisterResponse toResponseRegisterUser(UserEntity user) {
        if (user == null) {
            return null;
        }

        return new UserRegisterResponse(
                user.getFullname(),
                user.getEmail(),
                user.getCpf(),
                user.getPhone(),
                user.getOccupation(),
                user.getCreatedAt()
        );
    }

    public UserUpdateResponse toResponseUpdateUser(UserEntity user) {
        if (user == null) {
            return null;
        }

        return new UserUpdateResponse(
                user.getFullname(),
                user.getEmail(),
                user.getCpf(),
                user.getPhone(),
                user.getOccupation(),
                user.getUpdatedAt()
        );
    }

    public UserInternalResponse toResponseUser(UserEntity user) {
        if (user == null) {
            return null;
        }

        return new UserInternalResponse(
                user.getId(),
                user.getFullname(),
                user.getEmail(),
                user.getCpf(),
                user.getPhone(),
                user.getOccupation(),
                user.getCreatedAt() != null ? user.getCreatedAt().toString() : null,
                user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null
        );
    }
}