package redirex.shipping.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redirex.shipping.dto.RegisterUserDTO;
import redirex.shipping.entity.User;
import redirex.shipping.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterUserDTO dto) {
        try {
            User user = User.builder()
                    .fullname(dto.getFullname())
                    .email(dto.getEmail())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .cpf(dto.getCpf())
                    .phone(dto.getPhone())
                    .address(dto.getAddress())
                    .complement(dto.getComplement())
                    .city(dto.getCity())
                    .state(dto.getState())
                    .zipcode(dto.getZipcode())
                    .country(dto.getCountry())
                    .occupation(dto.getOccupation())
                    .role("ROLE_USER")
                    .build();
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Erro ao criar usuário: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar usuário: " + e.getMessage(), e);
        }
    }

    public User registerNewUser(RegisterUserDTO registerUserDTO) {
        return register(registerUserDTO);
    }

    public List<User> getAllUsers() {
        logger.info("Buscando todos os usuários");
        return userRepository.findAll();
    }
}