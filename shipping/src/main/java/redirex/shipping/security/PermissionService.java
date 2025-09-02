package redirex.shipping.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import redirex.shipping.security.CustomUnifiedUserDetailsService.CustomUserDetails;

import java.util.UUID;

@Service
public class PermissionService {


     // Retorna o usuário logado no sistema.
    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) auth.getPrincipal();
    }

     // Verifica se o usuário logado é o dono do recurso.
    public boolean isOwner(UUID resourceOwnerId) {
        return getCurrentUser().getId().equals(resourceOwnerId);
    }

     // Verifica se o usuário logado é o dono do recurso ou um admin.
    public boolean isOwnerOrAdmin(UUID resourceOwnerId) {
        CustomUserDetails user = getCurrentUser();
        return user.getId().equals(resourceOwnerId) || user.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

     // Verifica se o usuário logado é admin.

    public boolean isAdmin() {
        return getCurrentUser().getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
