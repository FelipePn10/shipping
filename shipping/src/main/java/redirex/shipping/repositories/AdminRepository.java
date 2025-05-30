package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.entity.UserEntity;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    Optional<AdminEntity> findByEmail(String email);
    Optional<UserEntity> findByAdministratorLoginCode(String AdministratorLoginCode);

    boolean existsByEmail(String email);
    boolean existsByAdministratorLoginCode(String AdministratorLoginCode);
    boolean existsByCpf(String cpf);

}