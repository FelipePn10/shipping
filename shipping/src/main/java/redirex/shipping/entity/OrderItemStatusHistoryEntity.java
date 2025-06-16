package redirex.shipping.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_item_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemStatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItemEntity orderItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderItemStatusEnum oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderItemStatusEnum newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_admin_id", nullable = false)
    private AdminEntity changedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Column(length = 500)
    private String notes; // Campo para observações (ex: motivo da mudança)

    // oldStatus/newStatus: Registra a transição de estados
    // changedBy: Admin responsável pela alteração
}