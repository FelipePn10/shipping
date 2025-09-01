package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import redirex.shipping.entity.PasswordResetToken;
import redirex.shipping.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByUserAndCodeAndUsedFalse(UserEntity user, String code);
    Optional<PasswordResetToken> findBySessionTokenAndUsedFalse(String sessionToken);
}
