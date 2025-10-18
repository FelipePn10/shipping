package redirex.shipping.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
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
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    // Lista de endpoints públicos que não requerem autenticação
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/public/",
            "/swagger-ui/",
            "/v3/api-docs",
            "/webjars/",
            "/swagger-resources",
            "/favicon.ico",
            "/error"
    );

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

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        logger.debug("Processando requisição: {} {}", method, requestPath);

        // Verifica se é um endpoint público
        if (isPublicEndpoint(requestPath)) {
            logger.debug("Endpoint público - ignorando autenticação: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String username = null;

        // Extrai o token JWT do header
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("Header Authorization ausente ou sem prefixo Bearer para: {}", requestPath);
            sendUnauthorizedError(response, "Token de autenticação não fornecido");
            return;
        }

        try {
            jwtToken = authHeader.substring(BEARER_PREFIX.length()).trim();

            // Validação básica do token
            if (jwtToken.isEmpty()) {
                logger.warn("Token JWT vazio");
                sendUnauthorizedError(response, "Token não fornecido");
                return;
            }

            if (jwtToken.split("\\.").length != 3) {
                logger.warn("Token JWT malformado - não possui 3 partes");
                sendUnauthorizedError(response, "Token malformado");
                return;
            }

            logger.debug("Token JWT recebido ({} caracteres) para: {}", jwtToken.length(), requestPath);

            // Verifica se o token está na blacklist
            if (tokenBlacklistService.isTokenBlacklisted(jwtToken)) {
                logger.warn("Token revogado tentou acessar: {}", requestPath);
                sendUnauthorizedError(response, "Token revogado");
                return;
            }

            // Extrai username do token
            username = jwtUtil.getUsernameFromToken(jwtToken);
            logger.debug("Username extraído do token: {}", username);

        } catch (ExpiredJwtException e) {
            logger.warn("Token expirado tentou acessar {}: {}", requestPath, e.getMessage());
            sendUnauthorizedError(response, "Token expirado");
            return;
        } catch (MalformedJwtException e) {
            logger.warn("Token malformado: {}", e.getMessage());
            sendUnauthorizedError(response, "Token malformado");
            return;
        } catch (SecurityException e) {
            logger.warn("Assinatura JWT inválida: {}", e.getMessage());
            sendUnauthorizedError(response, "Assinatura inválida");
            return;
        } catch (Exception e) {
            logger.error("Erro ao processar token JWT: {}", e.getMessage());
            sendUnauthorizedError(response, "Token inválido");
            return;
        }

        // Processa a autenticação
        try {
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.debug("Carregando UserDetails para: {}", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Valida o token
                if (jwtUtil.validateToken(jwtToken, userDetails)) {
                    logger.debug("Token válido para: {}", username);
                    setAuthenticationInContext(userDetails, request);
                    logger.debug("Autenticação configurada no contexto para: {}", username);
                } else {
                    logger.warn("Token inválido para: {}", username);
                    sendUnauthorizedError(response, "Token inválido");
                    return;
                }
            }
        } catch (UsernameNotFoundException e) {
            logger.error("Usuário não encontrado: {}", username);
            sendUnauthorizedError(response, "Usuário não encontrado");
            return;
        } catch (Exception e) {
            logger.error("Erro durante autenticação: {}", e.getMessage(), e);
            sendInternalError(response, "Erro de autenticação");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith) ||
                path.equals("/public/auth/v1/user/login") ||
                path.equals("/public/auth/v1/user/register");
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

    private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"error\": \"Unauthorized\", \"message\": \"%s\", \"status\": 401, \"timestamp\": \"%s\"}",
                message, java.time.LocalDateTime.now()
        );
        response.getWriter().write(jsonResponse);
    }

    private void sendInternalError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"error\": \"Internal Server Error\", \"message\": \"%s\", \"status\": 500, \"timestamp\": \"%s\"}",
                message, java.time.LocalDateTime.now()
        );
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return isPublicEndpoint(path);
    }
}