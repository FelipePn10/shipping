package redirex.shipping.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import redirex.shipping.repositories.AdminRepository;
import redirex.shipping.repositories.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Service
public class CustomUnifiedUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public CustomUnifiedUserDetailsService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Primeiro, tenta buscar como User
        return userRepository.findByEmail(username)
                .map(user -> new CustomUserDetails(
                        user.getId(),
                        user.getEmail(),
                        user.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
                ))
                .orElseGet(() ->
                        // Se não encontrar, tenta como Admin
                        adminRepository.findByEmail(username)
                                .map(admin -> new CustomUserDetails(
                                        admin.getId(),
                                        admin.getEmail(),
                                        admin.getPassword(),
                                        Collections.singletonList(new SimpleGrantedAuthority(admin.getRole()))
                                ))
                                .orElseThrow(() -> new UsernameNotFoundException("Usuário ou Admin não encontrado com email: " + username))
                );
    }

    public static class CustomUserDetails implements UserDetails {
        @Getter
        private final UUID id;
        private final String email;
        private final String password;
        private final Collection<? extends GrantedAuthority> authorities;

        public CustomUserDetails(UUID id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.authorities = authorities;
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
        public boolean isAccountNonExpired() { return true; }

        @Override
        public boolean isAccountNonLocked() { return true; }

        @Override
        public boolean isCredentialsNonExpired() { return true; }

        @Override
        public boolean isEnabled() { return true; }
    }
}
