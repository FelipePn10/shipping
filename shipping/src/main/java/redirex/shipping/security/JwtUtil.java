package redirex.shipping.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
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

    private SecretKey getSigningKey() {
        try {
            // Se a secretKey não foi configurada ou é muito curta, geramos uma automaticamente
            if (secretKey == null || secretKey.trim().isEmpty() || secretKey.length() < 64) {
                logger.warn("Chave JWT não configurada ou muito curta. Gerando chave segura automaticamente...");
                SecretKey generatedKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
                String base64Key = Base64.getEncoder().encodeToString(generatedKey.getEncoded());
                logger.info("CHAVE JWT GERADA AUTOMATICAMENTE (Adicione ao application.properties): jwt.secret={}", base64Key);
                return generatedKey;
            }

            // Decodifica a chave Base64
            byte[] keyBytes;
            try {
                keyBytes = Base64.getDecoder().decode(secretKey);
            } catch (IllegalArgumentException e) {
                // Se não for Base64 válido, usa a string diretamente como UTF-8
                logger.warn("Chave JWT não é Base64 válido. Usando como string UTF-8...");
                keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            }

            // Garante que a chave tenha tamanho suficiente para HS512
            if (keyBytes.length < 64) {
                logger.warn("Chave JWT muito curta ({} bytes). Expandindo para 64 bytes...", keyBytes.length);
                byte[] expandedKey = new byte[64];
                System.arraycopy(keyBytes, 0, expandedKey, 0, Math.min(keyBytes.length, 64));
                // Preenche o restante com zeros se necessário
                for (int i = keyBytes.length; i < 64; i++) {
                    expandedKey[i] = 0;
                }
                keyBytes = expandedKey;
            }

            return Keys.hmacShaKeyFor(keyBytes);

        } catch (Exception e) {
            logger.error("Erro ao criar chave de assinatura JWT: {}", e.getMessage(), e);
            throw new SecurityException("Falha na configuração da chave JWT", e);
        }
    }

    public String generateToken(String email, UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("email", email);
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        logger.debug("Gerando token JWT para: {}", subject);

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                    .signWith(getSigningKey())
                    .compact();
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
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);

            if (!isValid) {
                logger.warn("Token inválido para usuário: {}", username);
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
                    .setSigningKey(getSigningKey())
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

    // Métodos auxiliares para buscar IDs (mantidos para compatibilidade)
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