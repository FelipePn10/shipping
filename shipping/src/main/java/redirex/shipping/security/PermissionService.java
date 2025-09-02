package redirex.shipping.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import redirex.shipping.security.CustomUnifiedUserDetailsService.CustomUserDetails;

import java.util.UUID;

@Service("permissionService")
public class PermissionService {

    public boolean isOwner(UUID resourceOwnerId) {
        CustomUserDetails principal = (CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getId().equals(resourceOwnerId);
    }

    public boolean isOwnerOrAdmin(UUID resourceOwnerId) {
        CustomUserDetails principal = (CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin || principal.getId().equals(resourceOwnerId);
    }
}
