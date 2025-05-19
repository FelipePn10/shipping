package redirex.shipping.repositories;

import org.springframework.stereotype.Repository;
import redirex.shipping.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    Optional<AddressEntity> findByUserId(Long userId);
    Optional<AddressEntity> findByZipcode(String zipcode);
}