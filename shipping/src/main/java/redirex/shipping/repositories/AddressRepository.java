package redirex.shipping.repositories;

import org.springframework.stereotype.Repository;
import redirex.shipping.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
    Optional<AddressEntity> findByUserId(UUID userId);
    Optional<AddressEntity> findByZipcode(String zipcode);
}