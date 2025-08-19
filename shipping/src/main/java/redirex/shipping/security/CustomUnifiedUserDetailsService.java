package redirex.shipping.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.repositories.AdminRepository;
import redirex.shipping.repositories.UserRepository;

import java.util.Collections;

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
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
                ))
                .orElseGet(() ->
                        // Se não encontrar, tenta como Admin
                        adminRepository.findByEmail(username)
                                .map(admin -> new org.springframework.security.core.userdetails.User(
                                        admin.getEmail(),
                                        admin.getPassword(),
                                        Collections.singletonList(new SimpleGrantedAuthority(admin.getRole()))
                                ))
                                .orElseThrow(() -> new UsernameNotFoundException("Usuário ou Admin não encontrado com email: " + username))
                );
    }
}