package redirex.shipping.security;

import ch.qos.logback.classic.Logger;
import io.jsonwebtoken.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            return email.equals(userDetails.getUsername()) && !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            logger.error("Token expirado: {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            logger.error("Erro ao extrair username do token: {}", e.getMessage());
            return null;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public long getExpirationTimeInSeconds(String token) {
        Date expirationDate = extractExpiration(token);
        long expirationTimeMillis = expirationDate.getTime() - System.currentTimeMillis();
        return expirationTimeMillis > 0 ? expirationTimeMillis / 1000 : 0;
    }
}