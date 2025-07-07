package redirex.shipping.entity;

import jakarta.persistence.*;
import lombok.Data;
import redirex.shipping.enums.SubscriptionPlanEnum;
import redirex.shipping.enums.SubscriptionStatusEnum;


import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "subscriptions")
public class SubscriptionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String stripeSubscriptionId;

    @Column(nullable = false)
    private String planId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlanEnum planType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatusEnum status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime cancelledAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}