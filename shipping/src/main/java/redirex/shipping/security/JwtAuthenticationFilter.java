package redirex.shipping.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import redirex.shipping.service.TokenBlacklistService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   CustomUserDetailsService userDetailsService,
                                   TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(BEARER_PREFIX_LENGTH);

        try {
            if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                logger.warn("Attempt to use blacklisted token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token revoked");
                return;
            }

            String email = jwtUtil.extractUsername(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    setAuthenticationInContext(userDetails, request);
                }
            }
        } catch (ExpiredJwtException ex) {
            logger.warn("Expired JWT token: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
            return;
        } catch (UsernameNotFoundException ex) {
            logger.error("User not found: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        } catch (JwtException | IllegalArgumentException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthenticationInContext(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/public/auth/login") ||
                path.startsWith("/public/auth/register") ||
                path.startsWith("/user");
    }
}