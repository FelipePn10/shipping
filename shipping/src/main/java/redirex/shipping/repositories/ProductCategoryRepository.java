package redirex.shipping.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import redirex.shipping.entity.ProductCategoryEntity;

import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, UUID> {
    Optional<ProductCategoryEntity> findByName(String name);
}