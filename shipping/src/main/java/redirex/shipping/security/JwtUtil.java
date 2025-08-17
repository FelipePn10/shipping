package redirex.shipping.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(String email, UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        logger.debug("Gerando token para subject: {}", subject);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public UUID getUserIdFromToken(String token) {
        try {
            UUID userId = getClaimFromToken(token, claims -> claims.get("userId", UUID.class));
            if (userId == null) {
                logger.error("Nenhum userId encontrado no token");
                throw new IllegalArgumentException("Token não contém userId");
            }
            return userId;
        } catch (Exception e) {
            logger.error("Erro ao extrair userId do token: {}", e.getMessage());
            throw new IllegalArgumentException("Token inválido", e);
        }
    }

    public long getExpirationTimeInSeconds(String token) {
        try {
            Date expirationDate = getClaimFromToken(token, Claims::getExpiration);
            return (expirationDate.getTime() - System.currentTimeMillis()) / 1000;
        } catch (Exception e) {
            logger.error("Erro ao extrair tempo de expiração do token: {}", e.getMessage());
            throw new IllegalArgumentException("Token inválido", e);
        }
    }

    public UUID getUserIdFromUsername(String username) {
        logger.debug("Buscando userId para username: {}", username);
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("Usuário não encontrado para email: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado");
                });
        return user.getId();
    }

    public UUID getAdminIdFromUsername(String username) {
        logger.debug("Buscando adminId para username: {}", username);
        AdminEntity admin = adminRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("Admin não encontrado para email: {}", username);
                    return new UsernameNotFoundException("Admin não encontrado");
                });
        return admin.getId();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            if (!isValid) {
                logger.warn("Token inválido para username: {}", username);
            }
            return isValid;
        } catch (Exception e) {
            logger.error("Erro ao validar token: {}", e.getMessage());
            return false;
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getClaimFromToken(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.error("Erro ao verificar expiração do token: {}", e.getMessage());
            return true;
        }
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            logger.error("Erro ao extrair claim do token: {}", e.getMessage());
            throw new IllegalArgumentException("Token inválido", e);
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}