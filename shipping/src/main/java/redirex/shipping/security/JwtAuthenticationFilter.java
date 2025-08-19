package redirex.shipping.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
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

    private final JwtUtil jwtUtil;
    private final CustomUnifiedUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   CustomUnifiedUserDetailsService userDetailsService,
                                   TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Ignorar endpoints públicos
        if (path.startsWith("/public/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        // Verifica se o header existe e começa com "Bearer"
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            logger.debug("Nenhum token Bearer encontrado no cabeçalho Authorization");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extrai o token removendo o prefixo e espaços extras
            jwt = authHeader.substring(BEARER_PREFIX.length()).trim();

            // Validação básica do formato do token
            if (jwt.isEmpty() || jwt.split("\\.").length != 3) {
                logger.error("Token JWT malformado: {}", jwt);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token malformado");
                return;
            }

            logger.debug("Token JWT recebido: {}", jwt);

            // Verifica se o token está na blacklist
            if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                logger.warn("Tentativa de uso de token revogado");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token revogado");
                return;
            }

            // Extrai o username do token
            username = jwtUtil.getUsernameFromToken(jwt);
            logger.debug("Username extraído do token: {}", username);

        } catch (MalformedJwtException ex) {
            logger.error("Token JWT mal-formado: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
            return;
        } catch (SignatureException ex) {
            logger.error("Assinatura JWT inválida: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Assinatura inválida");
            return;
        } catch (Exception ex) {
            logger.error("Erro ao processar token JWT: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erro no token");
            return;
        }

        try {
            // Se temos um username e não há autenticação no contexto atual
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.debug("Carregando UserDetails para: {}", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Valida o token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    logger.debug("Token válido para: {}", username);
                    setAuthenticationInContext(userDetails, request);
                }
            }
        } catch (ExpiredJwtException ex) {
            logger.warn("Token JWT expirado: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado");
            return;
        } catch (UsernameNotFoundException ex) {
            logger.error("Usuário não encontrado: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não encontrado");
            return;
        } catch (Exception ex) {
            logger.error("Erro ao autenticar: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro de autenticação");
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
        logger.debug("Autenticação definida no contexto para: {}", userDetails.getUsername());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/public/auth/v1/user/login") ||
                path.startsWith("/public/auth/v1/user/register") ||
                path.startsWith("/public/user") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/public/auth/admin/v1/login");
    }
}