package redirex.shipping.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.repositories.AdminRepository;
import redirex.shipping.repositories.UserRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Value("${jwt.secret:}")
    private String secretKey;

    @Value("${jwt.expiration:86400}")
    private long expiration;

    // Chave fixa para garantir consistência
    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = initializeSigningKey();
        logger.info("JwtUtil inicializado com chave de {} bits", signingKey.getEncoded().length * 8);
    }

    private SecretKey initializeSigningKey() {
        try {
            // Se não há chave configurada ou é muito curta, cria uma fixa baseada no application name
            if (secretKey == null || secretKey.trim().isEmpty() || secretKey.length() < 32) {
                logger.warn("Chave JWT não configurada ou muito curta. Usando chave padrão...");

                // Cria uma chave consistente baseada em um seed fixo
                String defaultSeed = "redirex-shipping-default-secret-key-2025-for-hs512-algorithm";
                byte[] keyBytes = defaultSeed.getBytes(StandardCharsets.UTF_8);

                // Garante que tenha pelo menos 64 bytes para HS512
                byte[] secureKeyBytes = new byte[64];
                System.arraycopy(keyBytes, 0, secureKeyBytes, 0, Math.min(keyBytes.length, 64));

                // Preenche o restante se necessário
                if (keyBytes.length < 64) {
                    for (int i = keyBytes.length; i < 64; i++) {
                        secureKeyBytes[i] = (byte) i;
                    }
                }

                return Keys.hmacShaKeyFor(secureKeyBytes);
            }

            // Usa a chave configurada
            byte[] keyBytes;
            try {
                // Tenta decodificar como Base64
                keyBytes = Base64.getDecoder().decode(secretKey);
            } catch (IllegalArgumentException e) {
                // Se falhar, usa como string UTF-8
                logger.debug("Chave JWT não é Base64, usando como string UTF-8");
                keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            }

            // Expande para 64 bytes se necessário
            if (keyBytes.length < 64) {
                logger.debug("Expandindo chave JWT de {} para 64 bytes", keyBytes.length);
                byte[] expandedKey = new byte[64];
                System.arraycopy(keyBytes, 0, expandedKey, 0, Math.min(keyBytes.length, 64));
                for (int i = keyBytes.length; i < 64; i++) {
                    expandedKey[i] = (byte) (i % 256);
                }
                keyBytes = expandedKey;
            }

            return Keys.hmacShaKeyFor(keyBytes);

        } catch (Exception e) {
            logger.error("Erro crítico ao inicializar chave JWT: {}", e.getMessage(), e);
            throw new SecurityException("Falha na configuração da chave JWT", e);
        }
    }

    public String generateToken(String email, UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("email", email);
        claims.put("type", "ACCESS");
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        logger.debug("Gerando token JWT para: {}", subject);

        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expiration * 1000);

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(signingKey)
                    .compact();

            logger.debug("Token gerado com sucesso para: {}", subject);
            return token;

        } catch (Exception e) {
            logger.error("Erro ao criar token JWT: {}", e.getMessage(), e);
            throw new SecurityException("Falha na criação do token JWT", e);
        }
    }

    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UUID getUserIdFromToken(String token) {
        try {
            String userIdStr = extractClaim(token, claims -> claims.get("userId", String.class));
            if (userIdStr == null) {
                logger.error("UserId não encontrado no token JWT");
                throw new IllegalArgumentException("Token não contém userId");
            }
            return UUID.fromString(userIdStr);
        } catch (Exception e) {
            logger.error("Erro ao extrair userId do token: {}", e.getMessage());
            throw new IllegalArgumentException("Token inválido - userId ausente ou inválido", e);
        }
    }

    public long getExpirationTimeInSeconds(String token) {
        try {
            Date expirationDate = extractClaim(token, Claims::getExpiration);
            long remainingSeconds = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;
            return Math.max(0, remainingSeconds);
        } catch (Exception e) {
            logger.error("Erro ao extrair tempo de expiração do token: {}", e.getMessage());
            throw new IllegalArgumentException("Token inválido", e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) {
            logger.error("Erro ao verificar expiração do token: {}", e.getMessage());
            return true;
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            logger.debug("Validando token para usuário: {} vs {}", username, userDetails.getUsername());

            boolean usernameMatches = username.equals(userDetails.getUsername());
            boolean notExpired = !isTokenExpired(token);

            if (!usernameMatches) {
                logger.warn("Username não corresponde: token={}, userDetails={}", username, userDetails.getUsername());
            }
            if (!notExpired) {
                logger.warn("Token expirado para: {}", username);
            }

            boolean isValid = usernameMatches && notExpired;

            if (isValid) {
                logger.debug("Token válido para: {}", username);
            } else {
                logger.warn("Token inválido para: {}", username);
            }

            return isValid;

        } catch (ExpiredJwtException e) {
            logger.warn("Token JWT expirado para: {}", e.getClaims().getSubject());
            return false;
        } catch (MalformedJwtException e) {
            logger.warn("Token JWT malformado: {}", e.getMessage());
            return false;
        } catch (SecurityException e) {
            logger.warn("Assinatura JWT inválida: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Erro inesperado ao validar token: {}", e.getMessage());
            return false;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            logger.warn("Token expirado ao extrair claim: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao extrair claim do token: {}", e.getMessage());
            throw new IllegalArgumentException("Token inválido", e);
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("Token JWT expirado: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT malformado: {}", e.getMessage());
            throw new IllegalArgumentException("Token malformado", e);
        } catch (SecurityException e) {
            logger.error("Assinatura JWT inválida: {}", e.getMessage());
            throw new IllegalArgumentException("Assinatura inválida", e);
        } catch (Exception e) {
            logger.error("Erro ao analisar token JWT: {}", e.getMessage());
            throw new IllegalArgumentException("Token inválido", e);
        }
    }

    // Métodos auxiliares mantidos para compatibilidade
    public UUID getUserIdFromUsername(String username) {
        logger.debug("Buscando userId para: {}", username);
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("Usuário não encontrado: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado");
                });
        return user.getId();
    }

    public UUID getAdminIdFromUsername(String username) {
        logger.debug("Buscando adminId para: {}", username);
        AdminEntity admin = adminRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("Admin não encontrado: {}", username);
                    return new UsernameNotFoundException("Admin não encontrado");
                });
        return admin.getId();
    }
}