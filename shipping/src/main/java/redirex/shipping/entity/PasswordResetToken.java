package redirex.shipping.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 6)
    private String code; // código de verificação

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "session_token")
    private String sessionToken; // token temporário p/ troca de senha

    @Column(name = "session_expiry")
    private LocalDateTime sessionExpiry;

    @Column(nullable = false)
    private boolean used = false; // marca se já foi consumido
}
