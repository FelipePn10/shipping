package redirex.shipping.service;

import lombok.RequiredArgsConstructor;
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
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.repositories.UserWalletRepository;
import redirex.shipping.repositories.WalletTransactionRepository;
import redirex.shipping.exception.InsufficientBalanceException;
import redirex.shipping.exception.StripePaymentException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
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

    @Transactional
    public UserWalletEntity createInitialWallet(UserEntity user, CurrencyEnum cny) {
        CurrencyEnum currency = CurrencyEnum.CNY;
        logger.info("Creating initial CNY wallet for user: {}", user.getEmail());

        if (!SUPPORTED_CURRENCIES.contains(currency)) {
            throw new IllegalArgumentException("Unsupported currency for initial wallet: " + currency + ". Only CNY is supported.");
        }

        if (userWalletRepository.findByUserIdAndCurrency(user, currency).isPresent()) {
            logger.warn("User {} already has a {} wallet. Returning existing wallet.", user.getEmail(), currency);
            return userWalletRepository.findByUserIdAndCurrency(user, currency).get();
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
    public WalletTransactionResponse depositToWallet(Long userId, DepositRequestDto depositRequestDto) {
        logger.info("Attempting deposit for userId: {} with target CNY amount: {}", userId, depositRequestDto.getAmount());

        if (depositRequestDto.getAmount() == null || depositRequestDto.getAmount().compareTo(BigDecimal.valueOf(50)) <= 0) {
            throw new IllegalArgumentException("Target deposit amount (CNY) must be greater than zero.");
        }
        if (depositRequestDto.getPaymentMethodId() == null || depositRequestDto.getPaymentMethodId().isBlank()) {
            throw new IllegalArgumentException("Stripe PaymentMethod ID is required for deposit.");
        }

        CurrencyEnum walletCurrency = CurrencyEnum.CNY;
        BigDecimal targetCNYAmount = depositRequestDto.getAmount().setScale(2, RoundingMode.HALF_UP); // Valor que o usuário quer na carteira -> CNY

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found for userId: {}", userId);
                    return new IllegalArgumentException("User not found for userId: " + userId);
                });

        UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, walletCurrency)
                .orElseGet(() -> {
                    logger.info("CNY wallet not found for user {}, creating one.", user.getEmail());
                    return createInitialWallet(user, CurrencyEnum.CNY);
                });

        // Obter taxa de câmbio BRL para CNY
        BigDecimal brlToCnyRate = exchangeRateService.getExchangeRate(CurrencyEnum.BRL, CurrencyEnum.CNY);
        if (brlToCnyRate == null || brlToCnyRate.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Invalid BRL to CNY exchange rate: {}", brlToCnyRate);
            throw new IllegalStateException("Could not retrieve a valid BRL to CNY exchange rate.");
        }

        // Calcular o valor a ser cobrado em BRL
        // amountToChargeInBRL = targetCNYAmount / brlToCnyRate
        BigDecimal amountToChargeInBRL = targetCNYAmount.divide(brlToCnyRate, BRL_SCALE, RoundingMode.CEILING);
        long amountToChargeInBRLCents = amountToChargeInBRL.multiply(new BigDecimal("100")).longValue();

        logger.info("Target CNY: {}, BRL to CNY Rate: {}, Calculated BRL charge: {} ({} cents)",
                targetCNYAmount, brlToCnyRate, amountToChargeInBRL, amountToChargeInBRLCents);

        boolean paymentSuccessful;
        try {
            logger.info("Processing Stripe payment for userId: {}, amountInCents (BRL): {}, currency: BRL",
                    userId, amountToChargeInBRLCents);
            paymentSuccessful = stripeService.processPayment(depositRequestDto.getPaymentMethodId(), amountToChargeInBRLCents, CurrencyEnum.BRL);
        } catch (StripePaymentException e) {
            logger.error("Stripe payment failed for userId: {}. Amount BRL: {}. Reason: {}",
                    userId, amountToChargeInBRL, e.getMessage(), e);
            throw e;
        }

        if (!paymentSuccessful) {
            logger.error("Stripe payment (BRL) was not successful for userId: {}, but no exception was thrown.", userId);
            throw new StripePaymentException("Stripe payment processing in BRL failed for an unknown reason.");
        }

        logger.info("Stripe payment successful for userId: {}. Charged: {} BRL for target {} CNY",
                userId, amountToChargeInBRL, targetCNYAmount);

        // Cálculo de taxa sobre o valor alvo em CNY
        BigDecimal feeInCNY = targetCNYAmount.multiply(TRANSACTION_FEE_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netAmountInCNY = targetCNYAmount.subtract(feeInCNY);

        wallet.setBalance(wallet.getBalance().add(netAmountInCNY));
        userWalletRepository.save(wallet);

        String transactionDescription = String.format("Stripe Deposit. Charged %.2f BRL for %.2f CNY target.",
                amountToChargeInBRL, targetCNYAmount);

        WalletTransactionEntity transaction = WalletTransactionEntity.builder()
                .userId(user)
                .userWallet(wallet)
                .currency(walletCurrency) // Moeda da carteira e da transação principal
                .amount(netAmountInCNY) // Valor líquido creditado na carteira em CNY
                .type(WalletTransactionTypeEnum.DEPOSIT)

                .transactionFee(feeInCNY) // Taxa em CNY
                .originalAmountDeposited(targetCNYAmount) // Valor bruto intencionado em CNY
                .originalCurrencyDeposited(walletCurrency) // Moeda do valor bruto intencionado
                .createdAt(LocalDateTime.now())
                // campos para registrar o valor cobrado em BRL
                .chargedAmount(String.valueOf(amountToChargeInBRL))
                .chargedCurrency(String.valueOf(CurrencyEnum.BRL))
                .build();
        walletTransactionRepository.save(transaction);

        logger.info("Deposit completed for userId: {}. Net amount credited: {} {}, Fee: {} {}. Charged in BRL: {}",
                userId, netAmountInCNY, walletCurrency, feeInCNY, walletCurrency, amountToChargeInBRL);

        return WalletTransactionResponse.builder()
                .status("success")
                .userId(userId)
                .amount(netAmountInCNY)
                .fee(String.valueOf(feeInCNY))
                .currency(walletCurrency.toString())
                .chargedAmount(String.valueOf(amountToChargeInBRL))
                //.chargedCurrency(CurrencyEnum.BRL))
                .transactionDescription(transactionDescription)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public void debitFromWallet(Long userId, CurrencyEnum currency, BigDecimal amount, String transactionType,
                                String description, Long orderItemId, Long shipmentId) throws InsufficientBalanceException {
        if (currency != CurrencyEnum.CNY) {
            throw new IllegalArgumentException("Debit operations only supported for CNY currency. Requested: " + currency);
        }
        if (!SUPPORTED_CURRENCIES.contains(currency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }

        if (orderItemId != null && shipmentId == null) {
            debitForOrder(userId, currency, amount, description, orderItemId);
        } else if (shipmentId != null && orderItemId == null) {
            debitForShipment(userId, currency, amount, description, orderItemId, shipmentId);
        } else {
            throw new IllegalArgumentException("Either orderItemId or shipmentId must be provided for debit.");
        }
    }

    @Transactional
    public void debitForOrder(Long userId, CurrencyEnum currency, BigDecimal amount, String description, Long orderItemId)
            throws InsufficientBalanceException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order payment amount must be positive.");
        }
        if (currency != CurrencyEnum.CNY) {
            throw new IllegalArgumentException("Unsupported currency for order payment: " + currency + ". Only CNY is supported.");
        }
        if (orderItemId == null) {
            throw new IllegalArgumentException("OrderItemId is required for order payment.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));
        UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, currency)
                .orElseThrow(() -> new IllegalArgumentException("CNY Wallet not found for userId: " + userId + ". Please ensure a wallet exists."));

        if (wallet.getBalance().compareTo(amount) < 0) {
            logger.warn("Insufficient balance for order payment. UserId: {}, WalletBalance: {}, Amount: {}", userId, wallet.getBalance(), amount);
            throw new InsufficientBalanceException("Insufficient balance in wallet: " + wallet.getBalance() + " " + currency);
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        userWalletRepository.save(wallet);

        WalletTransactionEntity transaction = WalletTransactionEntity.builder()
                .userId(user)
                .userWallet(wallet)
                .currency(currency)
                .amount(amount.negate())
                .type(WalletTransactionTypeEnum.ORDER_PAYMENT)
                .relatedOrderItemId(orderItemId)
                .createdAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(transaction);

        logger.info("Order payment debited for userId: {}, amount: {} {}, orderItemId: {}",
                userId, amount, currency, orderItemId);
    }

    @Transactional
    public void debitForShipment(Long userId, CurrencyEnum currency, BigDecimal amount, String description,
                                 Long orderItemId, Long shipmentId) throws InsufficientBalanceException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Shipment payment amount must be positive.");
        }
        if (currency != CurrencyEnum.CNY) {
            throw new IllegalArgumentException("Unsupported currency for shipment payment: " + currency + ". Only CNY is supported.");
        }
        if (shipmentId == null) {
            throw new IllegalArgumentException("ShipmentId is required for shipment payment.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));
        UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, currency)
                .orElseThrow(() -> new IllegalArgumentException("CNY Wallet not found for userId: " + userId + ". Please ensure a wallet exists."));

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
                .type(WalletTransactionTypeEnum.SHIPMENT_PAYMENT)
                .relatedOrderItemId(orderItemId)
                .relatedShipmentId(shipmentId)
                .createdAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(transaction);

        logger.info("Shipment payment debited for userId: {}, amount: {} {}, shipmentId: {}",
                userId, amount, currency, shipmentId);
    }

    @Override
    @Transactional
    public void refundToWallet(Long userId, CurrencyEnum currency, BigDecimal amount, String description, Long orderItemId) {
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
    public BigDecimal getUserWalletBalance(Long userId, CurrencyEnum currency) {
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
    public List<UserWalletEntity> listUserWallets(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));
        return userWalletRepository.findByUserId(user);
    }

    @Override
    public List<WalletTransactionEntity> listWalletTransactions(Long userId, CurrencyEnum currencyFilter) {
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