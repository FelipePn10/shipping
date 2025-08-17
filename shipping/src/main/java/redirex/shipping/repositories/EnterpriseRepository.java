package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import redirex.shipping.entity.EnterpriseEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnterpriseRepository extends JpaRepository <EnterpriseEntity, UUID>{
    Optional<EnterpriseEntity> findByEmail(String email);
    Optional<EnterpriseEntity> findByCnpj(String cnpj);

}
