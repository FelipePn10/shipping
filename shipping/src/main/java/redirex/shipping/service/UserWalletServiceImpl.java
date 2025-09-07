package redirex.shipping.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.request.DepositRequestDto;
import redirex.shipping.dto.response.WalletTransactionResponse;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.entity.WalletTransactionEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionTypeEnum;
import redirex.shipping.exception.InsufficientBalanceException;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.exception.StripePaymentException;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.repositories.UserWalletRepository;
import redirex.shipping.repositories.WalletTransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserWalletServiceImpl implements UserWalletService {
    private static final Logger logger = LoggerFactory.getLogger(UserWalletServiceImpl.class);

    private final UserWalletRepository userWalletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;
    private final StripeService stripeService;
    private final ExchangeRateService exchangeRateService;

    private static final List<CurrencyEnum> SUPPORTED_CURRENCIES = List.of(CurrencyEnum.CNY);
    private static final BigDecimal TRANSACTION_FEE_PERCENTAGE = new BigDecimal("0.05");
    private static final int BRL_SCALE = 2;
    private static final String DEPOSIT_SUCCESS_STATUS = "success";
    private static final String PAYMENT_FAILED_ERROR = "payment_failed";

    public UserWalletServiceImpl(UserWalletRepository userWalletRepository,
                                 WalletTransactionRepository walletTransactionRepository,
                                 UserRepository userRepository,
                                 StripeService stripeService,
                                 ExchangeRateService exchangeRateService) {
        this.userWalletRepository = userWalletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.userRepository = userRepository;
        this.stripeService = stripeService;
        this.exchangeRateService = exchangeRateService;
    }

    @Transactional
    public UserWalletEntity createInitialWallet(UserEntity user, CurrencyEnum currency) {
        logger.info("Creating initial {} wallet for user: {}", currency, user.getEmail());

        if (!SUPPORTED_CURRENCIES.contains(currency)) {
            throw new IllegalArgumentException("Unsupported currency for initial wallet: " + currency + ". Only CNY is supported.");
        }

        Optional<UserWalletEntity> existingWallet = userWalletRepository.findByUserIdAndCurrency(user, currency);
        if (existingWallet.isPresent()) {
            logger.warn("User {} already has a {} wallet. Returning existing wallet.", user.getEmail(), currency);
            return existingWallet.get();
        }

        UserWalletEntity wallet = UserWalletEntity.builder()
                .userId(user)
                .currency(currency)
                .balance(BigDecimal.ZERO)
                .build();
        wallet = userWalletRepository.save(wallet);

        logger.info("Initial {} wallet created for user: {}", currency, user.getEmail());
        return wallet;
    }

    @Override
    @Transactional
    public WalletTransactionResponse depositToWallet(UUID userId, DepositRequestDto depositRequestDto) {
        logger.info("Attempting deposit for userId: {} with target CNY amount: {}", userId, depositRequestDto.amount());

        try {
            validateDepositRequest(depositRequestDto);

            UserEntity user = getUserEntity(userId);
            UserWalletEntity wallet = getUserWallet(user, CurrencyEnum.CNY);

            BigDecimal brlToCnyRate = getValidExchangeRate();
            BigDecimal amountToChargeInBRL = calculateAmountInBRL(depositRequestDto.amount(), brlToCnyRate);

            processStripePayment(depositRequestDto.paymentMethodId(), amountToChargeInBRL);
            return completeDepositTransaction(user, wallet, depositRequestDto.amount(), amountToChargeInBRL, brlToCnyRate);

        } catch (Exception e) {
            logger.error("Deposit failed for userId: {}", userId, e);
            return WalletTransactionResponse.createError(userId, "Deposit failed: " + e.getMessage());
        }
    }

    private void validateDepositRequest(DepositRequestDto depositRequestDto) {
        if (depositRequestDto.amount() == null || depositRequestDto.amount().compareTo(BigDecimal.valueOf(50)) <= 0) {
            throw new IllegalArgumentException("Target deposit amount (CNY) must be greater than zero.");
        }
        if (depositRequestDto.paymentMethodId() == null || depositRequestDto.paymentMethodId().isBlank()) {
            throw new IllegalArgumentException("Stripe PaymentMethod ID is required for deposit.");
        }
    }

    private UserEntity getUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found for userId: {}", userId);
                    return new IllegalArgumentException("User not found for userId: " + userId);
                });
    }

    private UserWalletEntity getUserWallet(UserEntity user, CurrencyEnum currency) {
        return userWalletRepository.findByUserIdAndCurrency(user, currency)
                .orElseGet(() -> {
                    logger.info("CNY wallet not found for user {}, creating one.", user.getEmail());
                    return createInitialWallet(user, CurrencyEnum.CNY);
                });
    }

    private BigDecimal getValidExchangeRate() {
        BigDecimal brlToCnyRate = exchangeRateService.getExchangeRate(CurrencyEnum.BRL, CurrencyEnum.CNY);
        if (brlToCnyRate == null || brlToCnyRate.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Invalid BRL to CNY exchange rate: {}", brlToCnyRate);
            throw new IllegalStateException("Could not retrieve a valid BRL to CNY exchange rate.");
        }
        return brlToCnyRate;
    }

    private BigDecimal calculateAmountInBRL(BigDecimal targetCNYAmount, BigDecimal exchangeRate) {
        return targetCNYAmount.divide(exchangeRate, BRL_SCALE, RoundingMode.CEILING);
    }

    private void processStripePayment(String paymentMethodId, BigDecimal amountToChargeInBRL) {
        long amountToChargeInBRLCents = amountToChargeInBRL.multiply(new BigDecimal("100")).longValue();

        logger.info("Processing Stripe payment, amountInCents (BRL): {}", amountToChargeInBRLCents);
        boolean paymentSuccessful = stripeService.processPayment(paymentMethodId, amountToChargeInBRLCents, CurrencyEnum.BRL);

        if (!paymentSuccessful) {
            logger.error("Stripe payment (BRL) was not successful");
            throw new StripePaymentException("Stripe payment processing in BRL failed for an unknown reason.");
        }

        logger.info("Stripe payment successful. Charged: {} BRL", amountToChargeInBRL);
    }

    @Transactional
    protected WalletTransactionResponse completeDepositTransaction(
            UserEntity user,
            UserWalletEntity wallet,
            BigDecimal targetCNYAmount,
            BigDecimal amountToChargeInBRL,
            BigDecimal exchangeRate
    ) {
        BigDecimal feeInCNY = calculateFee(targetCNYAmount);
        BigDecimal netAmountInCNY = targetCNYAmount.subtract(feeInCNY);

        updateWalletBalance(wallet, netAmountInCNY);
        WalletTransactionEntity transaction = createAndSaveTransaction(
                user, wallet, netAmountInCNY, feeInCNY, targetCNYAmount, amountToChargeInBRL
        );

        logger.info("Deposit completed for userId: {}. Net amount credited: {} {}, Fee: {} {}. Charged in BRL: {}",
                user.getId(), netAmountInCNY, wallet.getCurrency(), feeInCNY, wallet.getCurrency(), amountToChargeInBRL);

        return createSuccessResponse(transaction, wallet, user.getId(), feeInCNY, amountToChargeInBRL, netAmountInCNY);
    }

    private BigDecimal calculateFee(BigDecimal amount) {
        return amount.multiply(TRANSACTION_FEE_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
    }

    private void updateWalletBalance(UserWalletEntity wallet, BigDecimal amount) {
        wallet.setBalance(wallet.getBalance().add(amount));
        userWalletRepository.save(wallet);
    }

    private WalletTransactionEntity createAndSaveTransaction(
            UserEntity user,
            UserWalletEntity wallet,
            BigDecimal netAmountInCNY,
            BigDecimal feeInCNY,
            BigDecimal targetCNYAmount,
            BigDecimal amountToChargeInBRL
    ) {
        String transactionDescription = createTransactionDescription(amountToChargeInBRL, targetCNYAmount);

        WalletTransactionEntity transaction = WalletTransactionEntity.builder()
                .userId(user)
                .userWallet(wallet)
                .currency(wallet.getCurrency())
                .amount(netAmountInCNY)
                .type(WalletTransactionTypeEnum.DEPOSIT)
                .transactionFee(feeInCNY)
                .originalAmountDeposited(targetCNYAmount)
                .originalCurrencyDeposited(wallet.getCurrency())
                .createdAt(LocalDateTime.now())
                .chargedAmount(amountToChargeInBRL)
                .chargedCurrency(CurrencyEnum.BRL)
                .build();

        return walletTransactionRepository.save(transaction);
    }

    private String createTransactionDescription(BigDecimal amountInBRL, BigDecimal targetInCNY) {
        return String.format("Stripe Deposit. Charged %.2f BRL for %.2f CNY target.", amountInBRL, targetInCNY);
    }

    private WalletTransactionResponse createSuccessResponse(
            WalletTransactionEntity transaction,
            UserWalletEntity wallet,
            UUID userId,
            BigDecimal feeInCNY,
            BigDecimal amountToChargeInBRL,
            BigDecimal netAmountInCNY
    ) {
        return WalletTransactionResponse.createDepositSuccess(
                transaction.getId(),
                wallet.getWalletId(),
                userId,
                feeInCNY,
                wallet.getCurrency(),
                amountToChargeInBRL,
                netAmountInCNY,
                createTransactionDescription(amountToChargeInBRL, netAmountInCNY.add(feeInCNY)),
                transaction.getCreatedAt()
        );
    }
    @Override
    @Transactional
    public void debitFromWallet(UUID userId, CurrencyEnum currency, BigDecimal amount,
                                String transactionType, String description, UUID orderItemId,
                                UUID shipmentId, BigDecimal chargedAmount, CurrencyEnum chargedCurrency) {
        if (currency != CurrencyEnum.CNY) {
            throw new IllegalArgumentException("Debit operations only supported for CNY currency. Requested: " + currency);
        }

        if (orderItemId != null && shipmentId == null) {
            debitForOrder(userId, currency, amount, transactionType, description, orderItemId, chargedAmount, chargedCurrency);
        } else if (shipmentId != null && orderItemId == null) {
            debitForShipment(userId, currency, amount, transactionType, description, shipmentId, chargedAmount, chargedCurrency);
        } else {
            throw new IllegalArgumentException("Either orderItemId or shipmentId must be provided for debit, but not both.");
        }
    }

    @Transactional
    public void debitForOrder(UUID userId, CurrencyEnum currency, BigDecimal amount, String transactionType, String description,
                              UUID orderItemId, BigDecimal chargedAmount, CurrencyEnum chargedCurrency)
            throws InsufficientBalanceException {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order payment amount must be positive.");
        }
        if (orderItemId == null) {
            throw new IllegalArgumentException("OrderItemId is required for order payment.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for userId: " + userId));
        UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, currency)
                .orElseThrow(() -> new ResourceNotFoundException("CNY Wallet not found for userId: " + userId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            logger.warn("Insufficient balance for order payment. UserId: {}, WalletBalance: {}, Amount: {}", userId, wallet.getBalance(), amount);
            throw new InsufficientBalanceException("Insufficient balance in wallet: " + wallet.getBalance() + " " + currency);
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        userWalletRepository.save(wallet);

        WalletTransactionEntity transaction = WalletTransactionEntity.builder()
                .userWallet(wallet)
                .userId(user)
                .type(WalletTransactionTypeEnum.valueOf(transactionType))
                .currency(currency)
                .amount(amount.negate())
                .relatedOrderItemId(orderItemId)
                .relatedShipmentId(null)
                .chargedAmount(chargedAmount)
                .chargedCurrency(chargedCurrency)
                .build();

        walletTransactionRepository.save(transaction);

        logger.info("Order payment debited for userId: {}, amount: {} {}, orderItemId: {}",
                userId, amount, currency, orderItemId);
    }

    @Transactional
    public void debitForShipment(UUID userId, CurrencyEnum currency, BigDecimal amount, String transactionType, String description,
                                 UUID shipmentId, BigDecimal chargedAmount, CurrencyEnum chargedCurrency) throws InsufficientBalanceException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Shipment payment amount must be positive.");
        }
        if (shipmentId == null) {
            throw new IllegalArgumentException("ShipmentId is required for shipment payment.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for userId: " + userId));
        UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, currency)
                .orElseThrow(() -> new ResourceNotFoundException("CNY Wallet not found for userId: " + userId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            logger.warn("Insufficient balance for shipment payment. UserId: {}, WalletBalance: {}, Amount: {}", userId, wallet.getBalance(), amount);
            throw new InsufficientBalanceException("Insufficient balance in wallet: " + wallet.getBalance() + " " + currency);
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        userWalletRepository.save(wallet);

        WalletTransactionEntity transaction = WalletTransactionEntity.builder()
                .userId(user)
                .userWallet(wallet)
                .currency(currency)
                .amount(amount.negate())
                .type(WalletTransactionTypeEnum.valueOf(transactionType))
                .relatedOrderItemId(null)
                .relatedShipmentId(shipmentId)
                .chargedAmount(chargedAmount)
                .chargedCurrency(chargedCurrency)
                .build();
        walletTransactionRepository.save(transaction);

        logger.info("Shipment payment debited for userId: {}, amount: {} {}, shipmentId: {}",
                userId, amount, currency, shipmentId);
    }

    @Override
    @Transactional
    public void refundToWallet(UUID userId, CurrencyEnum currency, BigDecimal amount, String description, UUID orderItemId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive.");
        }
        if (currency != CurrencyEnum.CNY) {
            throw new IllegalArgumentException("Unsupported currency for refund: " + currency + ". Only CNY is supported.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));
        UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, currency)
                .orElseThrow(() -> new IllegalArgumentException("CNY Wallet not found for userId: " + userId + ". Please ensure a wallet exists."));

        wallet.setBalance(wallet.getBalance().add(amount));
        userWalletRepository.save(wallet);

        WalletTransactionEntity transaction = WalletTransactionEntity.builder()
                .userId(user)
                .userWallet(wallet)
                .currency(currency)
                .amount(amount)
                .type(WalletTransactionTypeEnum.REFUND)
                .relatedOrderItemId(orderItemId)
                .createdAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(transaction);

        logger.info("Refund credited to userId: {}, amount: {} {}, orderItemId: {}",
                userId, amount, currency, orderItemId != null ? orderItemId : "N/A");
    }

    @Override
    public BigDecimal getUserWalletBalance(UUID userId, CurrencyEnum currency) {
        if (currency != CurrencyEnum.CNY) {
            logger.warn("Attempt to get balance for unsupported currency: {} for userId: {}", currency, userId);
            throw new IllegalArgumentException("Balance check only supported for CNY currency. Requested: " + currency);
        }
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));
        UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, currency)
                .orElseThrow(() -> new IllegalArgumentException("CNY Wallet not found for userId: " + userId));
        return wallet.getBalance();
    }

    @Override
    public List<UserWalletEntity> listUserWallets(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));
        return userWalletRepository.findByUserId(user);
    }

    @Override
    public List<WalletTransactionEntity> listWalletTransactions(UUID userId, CurrencyEnum currencyFilter) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));

        if (currencyFilter != null) {
            if (currencyFilter != CurrencyEnum.CNY) {
                throw new IllegalArgumentException("Transaction listing only supported for CNY currency. Requested: " + currencyFilter);
            }
            UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, currencyFilter)
                    .orElseThrow(() -> new IllegalArgumentException("CNY Wallet not found for userId: " + userId + " and currency " + currencyFilter));
            return walletTransactionRepository.findByUserWallet(wallet);
        }
        return walletTransactionRepository.findAllByUserId(user.getId());
    }
}