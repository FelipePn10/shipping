package redirex.shipping.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_item_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemPhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String imageUrl;  // URL da imagem no storage (S3, etc.)

    private String description;

    @CreationTimestamp
    private LocalDateTime uploadedAt;

    // Relacionamento com pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItemEntity orderItem;

    // Relacionamento com admin que fez upload
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_admin_id")
    private AdminEntity uploadedBy;
}