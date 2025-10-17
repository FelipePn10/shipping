package redirex.shipping.service;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.internal.UserInternalResponse;
import redirex.shipping.dto.request.UpdateUserRequest;
import redirex.shipping.dto.response.DeleteUserResponse;
import redirex.shipping.dto.response.UserRegisterResponse;
import redirex.shipping.dto.request.RegisterUserRequest;
import redirex.shipping.dto.response.UserUpdateResponse;
import redirex.shipping.entity.*;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.OrderItemStatusEnum;
import redirex.shipping.exception.*;
import redirex.shipping.mapper.UserMapper;
import redirex.shipping.repositories.*;
import redirex.shipping.service.email.UserEmailService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
    private final WarehouseRepository warehouseRepository;
    private final UserEmailService userEmailService;
    private final OrderItemRepository orderItemRepository;
    private final UserWalletRepository userWalletRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserWalletServiceImpl userWalletService, WelcomeCouponService welcomeCouponService, UserCouponRepository userCouponRepository, UserMapper userMapper, WarehouseService warehouseService, WarehouseRepository warehouseRepository, UserEmailService userEmailService, OrderItemRepository orderItemRepository, UserWalletRepository userWalletRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userWalletService = userWalletService;
        this.welcomeCouponService = welcomeCouponService;
        this.userCouponRepository = userCouponRepository;
        this.userMapper = userMapper;
        this.warehouseService = warehouseService;
        this.warehouseRepository = warehouseRepository;
        this.userEmailService = userEmailService;
        this.orderItemRepository = orderItemRepository;
        this.userWalletRepository = userWalletRepository;
    }

    @Override
    @Transactional
    public UserRegisterResponse registerUser(@Valid RegisterUserRequest request) {
        logger.info("Registering user with email: {}", request.email());
        validateUserNotExists(request.email(), request.cpf());

        try {
            // Criar cupom de boas-vindas
            CouponEntity welcomeCoupon = welcomeCouponService.createWelcomeCoupon();

            UserEntity user = UserEntity.builder()
                    .fullname(request.fullname())
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .cpf(request.cpf())
                    .phone(request.phone())
                    .occupation(request.occupation())
                    .role("ROLE_USER")
                    .createdAt(LocalDateTime.now())
                    .build();
            if (welcomeCoupon != null) {
                user.getCoupons().add(welcomeCoupon);
            }

            user = userRepository.save(user);

            WarehouseEntity warehouse = warehouseService.createWarehouseForUser(user);
            user.setWarehouse(warehouse);

            user = userRepository.save(user);

            // (CNY, saldo zero)
            userWalletService.createInitialWallet(user, CurrencyEnum.CNY);

            // Criar UserCouponEntity para rastreamento adicional
            assert welcomeCoupon != null;
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

            logger.info("User registered successfully with email: {}", request.email());

            // email de boas-vindas
            try {
                logger.info("Email sent successfully: {}", request.email());
                userEmailService.sendWelcomeEmail(request.email(), request.fullname());
            } catch (SendeEmailWelcomeException e) {
                logger.error("Error sending email, error reported to server.", e.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            return userMapper.toResponseRegisterUser(user);

        } catch (Exception e) {
            logger.error("Failed to register user with email: {}", request.email(), e);
            throw new UserRegistrationException("Failed to register user", e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("@permissionService.isOwnerOrAdmin(#dto.userId)")
    public UserUpdateResponse updateUserProfile(UUID userId, @Valid UpdateUserRequest request) {
        logger.info("Updating user profile for ID: {}", userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        if (!request.email().equals(user.getEmail())) {
            validateEmailNotExists(request.email());
        }

        user.setFullname(request.fullname());
        user.setEmail(request.email());
        user.setCpf(request.cpf());
        user.setPhone(request.phone());
        user.setOccupation(request.occupation());

        user = userRepository.save(user);
        logger.info("User profile updated successfully: {}", user.getEmail());
        return userMapper.toResponseUpdateUser(user);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteUserProfile(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        checkUserWalletBalance(user);

        checkUserPendingOrders(user);

        user.getCoupons().clear();
        userRepository.save(user);

        if (user.getWallet() != null) {
            userWalletRepository.delete(user.getWallet());
        }

        if (user.getWarehouse() != null) {
            warehouseRepository.delete(user.getWarehouse());
        }

        userRepository.delete(user);
        logger.info("User deleted successfully: {}", user.getEmail());
    }

    private void checkUserWalletBalance(UserEntity user) {
        if (user.getWallet() != null) {
            UserWalletEntity wallet = userWalletRepository.findById(user.getWallet().getWalletId())
                    .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + user.getId()));

            if (wallet.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                throw new UserHasBalanceException(
                        "Cannot delete user with ID " + user.getId() +
                                " because they have a balance of " + wallet.getBalance() +
                                " in their wallet. Please transfer or withdraw the balance before deletion."
                );
            }
        }
    }

    private void checkUserPendingOrders(UserEntity user) {
        List<OrderItemEntity> pendingOrders = orderItemRepository.findByUserAndStatusNot(user, OrderItemStatusEnum.IN_WAREHOUSE);

        if (!pendingOrders.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Cannot delete user with ID ").append(user.getId())
                    .append(" because they have ").append(pendingOrders.size())
                    .append(" orders with status different from in warehouse:\n");

            for (OrderItemEntity order : pendingOrders) {
                errorMessage.append("- Order ID: ").append(order.getId())
                        .append(", Status: ").append(order.getStatus()).append("\n");
            }

            throw new UserHasPendingOrdersException(errorMessage.toString());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserInternalResponse findUserById(UUID userId) {
        logger.info("Finding user by ID: {}", userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        return userMapper.toResponseUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UUID findUserIdByEmail(String email) {
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