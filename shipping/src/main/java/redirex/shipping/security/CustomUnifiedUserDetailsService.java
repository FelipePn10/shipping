package redirex.shipping.security;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.repositories.AdminRepository;
import redirex.shipping.repositories.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
public class CustomUnifiedUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public CustomUnifiedUserDetailsService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Buscando UserDetails para: {}", username);

        // Primeiro tenta buscar como User normal
        return userRepository.findByEmail(username)
                .map(user -> {
                    log.debug("Usuário encontrado: {}", user.getEmail());
                    return new CustomUserDetails(
                            user.getId(),
                            user.getEmail(),
                            user.getPassword(),
                            Collections.singletonList(new SimpleGrantedAuthority(user.getRole())),
                            "USER"
                    );
                })
                .orElseGet(() ->
                        // Se não encontrar, tenta como Admin
                        adminRepository.findByEmail(username)
                                .map(admin -> {
                                    log.debug("Admin encontrado: {}", admin.getEmail());
                                    return new CustomUserDetails(
                                            admin.getId(),
                                            admin.getEmail(),
                                            admin.getPassword(),
                                            Collections.singletonList(new SimpleGrantedAuthority(admin.getRole())),
                                            "ADMIN"
                                    );
                                })
                                .orElseThrow(() -> {
                                    log.error("Usuário/Admin não encontrado com email: {}", username);
                                    return new UsernameNotFoundException("Credenciais inválidas");
                                })
                );
    }

    @Getter
    public static class CustomUserDetails implements UserDetails {
        private final UUID id;
        private final String email;
        private final String password;
        private final Collection<? extends GrantedAuthority> authorities;
        private final String userType;

        public CustomUserDetails(UUID id, String email, String password,
                                 Collection<? extends GrantedAuthority> authorities, String userType) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.authorities = authorities;
            this.userType = userType;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}