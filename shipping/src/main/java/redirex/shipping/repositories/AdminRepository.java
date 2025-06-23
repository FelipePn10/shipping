package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.AdminEntity;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    Optional<AdminEntity> findByEmail(String email);
    Optional<AdminEntity> findByAdministratorLoginCode(String AdministratorLoginCode);

    boolean existsByEmail(String email);
    boolean existsByAdministratorLoginCode(String AdministratorLoginCode);
    boolean existsByCpf(String cpf);
    List<AdminEntity> findByRole(String role);

    Long id(Long id);
}