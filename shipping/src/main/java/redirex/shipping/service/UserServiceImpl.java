package redirex.shipping.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.response.UserResponse;
import redirex.shipping.dto.RegisterUserDTO;
import redirex.shipping.entity.CouponEntity;
import redirex.shipping.entity.UserCouponEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.WarehouseEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.exception.SendeEmailWelcomeException;
import redirex.shipping.exception.UserRegistrationException;
import redirex.shipping.mapper.UserMapper;
import redirex.shipping.repositories.UserCouponRepository;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.service.email.UserEmailService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserWalletServiceImpl userWalletService;
    private final WelcomeCouponService welcomeCouponService;
    private final UserCouponRepository userCouponRepository;
    private final UserMapper userMapper;
    private final WarehouseService warehouseService;
    private final UserEmailService userEmailService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserWalletServiceImpl userWalletService, WelcomeCouponService welcomeCouponService, UserCouponRepository userCouponRepository, UserMapper userMapper, WarehouseService warehouseService, UserEmailService userEmailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userWalletService = userWalletService;
        this.welcomeCouponService = welcomeCouponService;
        this.userCouponRepository = userCouponRepository;
        this.userMapper = userMapper;
        this.warehouseService = warehouseService;
        this.userEmailService = userEmailService;
    }

    @Override
    @Transactional
    public UserResponse registerUser(@Valid RegisterUserDTO dto) {
        logger.info("Registering user with email: {}", dto.getEmail());
        validateUserNotExists(dto.getEmail(), dto.getCpf());

        try {
            // Criar cupom de boas-vindas
            CouponEntity welcomeCoupon = welcomeCouponService.createWelcomeCoupon();

            UserEntity user = UserEntity.builder()
                    .fullname(dto.getFullname())
                    .email(dto.getEmail())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .cpf(dto.getCpf())
                    .phone(dto.getPhone())
                    .occupation(dto.getOccupation())
                    .role("ROLE_USER")
                    .coupon(welcomeCoupon)
                    .build();

            // Salvar o UserEntity primeiro
            user = userRepository.save(user);

            // Criar warehouse padrão
            WarehouseEntity warehouse = warehouseService.createWarehouseForUser(user);
            user.setWarehouse(warehouse);

            // Salvar novamente para persistir a associação com o warehouse
            user = userRepository.save(user);

            // Criar carteira inicial (CNY, saldo zero)
            userWalletService.createInitialWallet(user, CurrencyEnum.CNY);

            // Criar UserCouponEntity para rastreamento adicional
            UserCouponEntity userCoupon = UserCouponEntity.builder()
                    .user(user)
                    .coupon(welcomeCoupon)
                    .couponCode(welcomeCoupon.getCode())
                    .discountPercentage(welcomeCoupon.getDiscountPercentage())
                    .currency(CurrencyEnum.CNY)
                    .used(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            userCouponRepository.save(userCoupon);

            logger.info("User registered successfully with email: {}", dto.getEmail());

            // Enviar o email de boas-vindas
            try {
                logger.info("Email sent successfully: {}", dto.getEmail());
                userEmailService.sendWelcomeEmail(dto.getEmail(), dto.getFullname());
            } catch (SendeEmailWelcomeException e) {
                logger.error("Error sending email, error reported to server.", e.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            return userMapper.toResponse(user);
        } catch (Exception e) {
            logger.error("Failed to register user with email: {}", dto.getEmail(), e);
            throw new UserRegistrationException("Failed to register user", e);
        }
    }

    @Override
    @Transactional
    public UserResponse updateUserProfile(Long id, @Valid RegisterUserDTO dto) {
        logger.info("Updating user profile for ID: {}", id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));

        if (!dto.getEmail().equals(user.getEmail())) {
            validateEmailNotExists(dto.getEmail());
        }
        if (!dto.getCpf().equals(user.getCpf())) {
            validateCpfNotExists(dto.getCpf());
        }

        user.setFullname(dto.getFullname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setOccupation(dto.getOccupation());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user = userRepository.save(user);
        logger.info("User profile updated successfully: {}", user.getEmail());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {
        logger.info("Finding user by ID: {}", id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Long findUserIdByEmail(String email) {
        logger.info("Finding user ID by email: {}", email);
        return userRepository.findByEmail(email)
                .map(UserEntity::getId)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }

    private void validateUserNotExists(String email, String cpf) {
        validateEmailNotExists(email);
        validateCpfNotExists(cpf);
    }

    private void validateEmailNotExists(String email) {
        Optional<UserEntity> existingUserByEmail = userRepository.findByEmail(email);
        if (existingUserByEmail.isPresent()) {
            logger.warn("Attempt to use duplicate email: {}", email);
            throw new UserRegistrationException("Email already registered");
        }
    }

    private void validateCpfNotExists(String cpf) {
        Optional<UserEntity> existingUserByCpf = userRepository.findByCpf(cpf);
        if (existingUserByCpf.isPresent()) {
            logger.warn("Attempt to use duplicate CPF: {}", cpf);
            throw new UserRegistrationException("CPF already registered");
        }
    }
}