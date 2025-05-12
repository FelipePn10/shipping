package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.AdminEntity;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    Optional<AdminEntity> findByEmail(String email);
}