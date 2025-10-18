package redirex.shipping.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import redirex.shipping.entity.AddressEntity;
import redirex.shipping.repositories.AddressRepository;
import redirex.shipping.security.CustomUnifiedUserDetailsService.CustomUserDetails;

import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionService {

    private final AddressRepository addressRepository;

    public PermissionService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    // Retorna o usuário logado no sistema.
    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new SecurityException("Usuário não autenticado");
        }
        return (CustomUserDetails) auth.getPrincipal();
    }

    // Verifica se o usuário logado é o dono do recurso.
    public boolean isOwner(UUID resourceOwnerId) {
        try {
            CustomUserDetails currentUser = getCurrentUser();
            return currentUser.getId().equals(resourceOwnerId);
        } catch (Exception e) {
            return false;
        }
    }

    // Verifica se o usuário logado é o dono do recurso ou um admin.
    public boolean isOwnerOrAdmin(UUID resourceOwnerId) {
        try {
            CustomUserDetails user = getCurrentUser();
            boolean isOwner = user.getId().equals(resourceOwnerId);
            boolean isAdmin = user.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            return isOwner || isAdmin;
        } catch (Exception e) {
            return false;
        }
    }

    // Verifica se o usuário logado é admin.
    public boolean isAdmin() {
        try {
            CustomUserDetails user = getCurrentUser();
            return user.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        } catch (Exception e) {
            return false;
        }
    }

    // Verifica se o usuário logado é o dono do endereço (por zipcode) ou admin.
    public boolean isOwnerOrAdminByZipcode(String zipcode) {
        try {
            CustomUserDetails user = getCurrentUser();

            // Se for admin, permite
            if (isAdmin()) {
                return true;
            }

            // Busca o endereço pelo zipcode e verifica se pertence ao usuário
            Optional<AddressEntity> address = addressRepository.findByZipcode(zipcode);
            return address.isPresent() && address.get().getUser().getId().equals(user.getId());

        } catch (Exception e) {
            return false;
        }
    }
}