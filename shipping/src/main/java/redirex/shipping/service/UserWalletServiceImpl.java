package redirex.shipping.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.request.DepositRequestDto;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.UserWalletEntity;
import redirex.shipping.entity.WalletTransactionEntity;
import redirex.shipping.enums.CurrencyEnum;
import redirex.shipping.enums.WalletTransactionTypeEnum;
import redirex.shipping.repositories.UserRepository;
import redirex.shipping.repositories.UserWalletRepository;
import redirex.shipping.repositories.WalletTransactionRepository;
import redirex.shipping.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserWalletServiceImpl implements UserWalletService {
    private static final Logger logger = LoggerFactory.getLogger(UserWalletServiceImpl.class);

    private final UserWalletRepository userWalletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final ExchangeRateService exchangeRateService;
    private final UserRepository userRepository;

    private static final List<CurrencyEnum> SUPPORTED_CURRENCIES = List.of(CurrencyEnum.CNY);
    private static final BigDecimal TRANSACTION_FEE_PERCENTAGE = new BigDecimal("0.05");

    @Transactional
    public UserWalletEntity createInitialWallet(UserEntity user, CurrencyEnum currency) {
        logger.info("Creating initial wallet for user: {} with currency: {}", user.getEmail(), currency);

        if (!SUPPORTED_CURRENCIES.contains(currency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }

        UserWalletEntity wallet = UserWalletEntity.builder()
                .userId(user)
                .currency(currency)
                .balance(BigDecimal.ZERO)
                .build();
        wallet = userWalletRepository.save(wallet);

        logger.info("Initial wallet created for user: {}", user.getEmail());
        return wallet;
    }

    @Override
    @Transactional
    public void depositToWallet(Long userId, DepositRequestDto depositRequestDto) {
        // Validações
        if (depositRequestDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
        if (!SUPPORTED_CURRENCIES.contains(depositRequestDto.getCurrency())) {
            throw new IllegalArgumentException("Unsupported currency: " + depositRequestDto.getCurrency());
        }

        // Busca o usuário
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));

        // Obtém a carteira do usuário
        UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, depositRequestDto.getCurrency())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for userId: " + userId));

        // Converte o valor para a moeda da carteira, se necessário
        CurrencyEnum sourceCurrency;
        try {
            sourceCurrency = CurrencyEnum.valueOf(depositRequestDto.getSourceCurrency());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid source currency: " + depositRequestDto.getSourceCurrency());
        }

        BigDecimal amountInWalletCurrency = depositRequestDto.getCurrency().equals(sourceCurrency)
                ? depositRequestDto.getAmount()
                : depositRequestDto.getAmount().multiply(
                exchangeRateService.getExchangeRate(sourceCurrency, depositRequestDto.getCurrency()));

        // Calcula a taxa
        BigDecimal fee = amountInWalletCurrency.multiply(TRANSACTION_FEE_PERCENTAGE);
        BigDecimal netAmount = amountInWalletCurrency.subtract(fee);

        // Atualiza o saldo
        wallet.setBalance(wallet.getBalance().add(netAmount));
        userWalletRepository.save(wallet);

        // Cria a transação
        WalletTransactionEntity transaction = WalletTransactionEntity.builder()
                .userId(user)
                .userWallet(wallet)
                .currency(depositRequestDto.getCurrency())
                .amount(netAmount)
                .type(WalletTransactionTypeEnum.DEPOSIT)
                .description("Deposit from " + depositRequestDto.getSourceCurrency())
                .transactionFee(fee)
                .originalAmountDeposited(depositRequestDto.getAmount())
                .originalCurrencyDeposited(sourceCurrency)
                .createdAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(transaction);

        logger.info("Deposit completed for userId: {}, amount: {}, currency: {}", userId, netAmount, depositRequestDto.getCurrency());
    }

    @Override
    @Transactional
    public void debitFromWallet(Long userId, CurrencyEnum currency, BigDecimal amount, String transactionType,
                                String description, Long orderItemId, Long shipmentId) throws InsufficientBalanceException {
        if (orderItemId != null && shipmentId == null) {
            debitForOrder(userId, currency, amount, description, orderItemId);
        } else if (shipmentId != null) {
            debitForShipment(userId, currency, amount, description, orderItemId, shipmentId);
        } else {
            throw new IllegalArgumentException("Either orderItemId or shipmentId must be provided");
        }
    }

    @Transactional
    public void debitForOrder(Long userId, CurrencyEnum currency, BigDecimal amount, String description, Long orderItemId)
            throws InsufficientBalanceException {
        // Validações
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order payment amount must be positive");
        }
        if (!SUPPORTED_CURRENCIES.contains(currency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
        if (orderItemId == null) {
            throw new IllegalArgumentException("OrderItemId is required for order payment");
        }

        // Busca o usuário
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));

        // Obtém a carteira
        UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, currency)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for userId: " + userId));

        // Verifica saldo
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in wallet: " + wallet.getBalance());
        }

        // Debita o valor
        wallet.setBalance(wallet.getBalance().subtract(amount));
        userWalletRepository.save(wallet);

        // Cria a transação
        WalletTransactionEntity transaction = WalletTransactionEntity.builder()
                .userId(user)
                .userWallet(wallet)
                .currency(currency)
                .amount(amount.negate()) // Valor negativo para débito
                .type(WalletTransactionTypeEnum.ORDER_PAYMENT)
                .description(description)
                .relatedOrderItemId(orderItemId)
                .createdAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(transaction);

        logger.info("Order payment completed for userId: {}, amount: {}, currency: {}, orderItemId: {}",
                userId, amount, currency, orderItemId);
    }

    @Transactional
    public void debitForShipment(Long userId, CurrencyEnum currency, BigDecimal amount, String description,
                                 Long orderItemId, Long shipmentId) throws InsufficientBalanceException {
        // Validações
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Shipment payment amount must be positive");
        }
        if (!SUPPORTED_CURRENCIES.contains(currency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
        if (shipmentId == null) {
            throw new IllegalArgumentException("ShipmentId is required for shipment payment");
        }

        // Busca o usuário
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));

        // Obtém a carteira
        UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, currency)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for userId: " + userId));

        // Verifica saldo
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in wallet: " + wallet.getBalance());
        }

        // Debita o valor
        wallet.setBalance(wallet.getBalance().subtract(amount));
        userWalletRepository.save(wallet);

        // Cria a transação
        WalletTransactionEntity transaction = WalletTransactionEntity.builder()
                .userId(user)
                .userWallet(wallet)
                .currency(currency)
                .amount(amount.negate())
                .type(WalletTransactionTypeEnum.SHIPMENT_PAYMENT)
                .description(description)
                .relatedOrderItemId(orderItemId)
                .relatedShipmentId(shipmentId)
                .createdAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(transaction);

        logger.info("Shipment payment completed for userId: {}, amount: {}, currency: {}, shipmentId: {}",
                userId, amount, currency, shipmentId);
    }

    @Override
    @Transactional
    public void refundToWallet(Long userId, CurrencyEnum currency, BigDecimal amount, String description, Long orderItemId) {
        // Validações
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }
        if (!SUPPORTED_CURRENCIES.contains(currency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }

        // Busca o usuário
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));

        // Obtém a carteira
        UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, currency)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for userId: " + userId));

        // Credita o valor
        wallet.setBalance(wallet.getBalance().add(amount));
        userWalletRepository.save(wallet);

        // Cria a transação
        WalletTransactionEntity transaction = WalletTransactionEntity.builder()
                .userId(user)
                .userWallet(wallet)
                .currency(currency)
                .amount(amount)
                .type(WalletTransactionTypeEnum.REFUND)
                .description(description)
                .relatedOrderItemId(orderItemId)
                .createdAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(transaction);

        logger.info("Refund completed for userId: {}, amount: {}, currency: {}", userId, amount, currency);
    }

    @Override
    public BigDecimal getUserWalletBalance(Long userId, CurrencyEnum currency) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));

        UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, currency)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for userId: " + userId));
        return wallet.getBalance();
    }

    @Override
    public List<UserWalletEntity> listUserWallets(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));
        return userWalletRepository.findByUserId(user);
    }

    @Override
    public List<WalletTransactionEntity> listWalletTransactions(Long userId, CurrencyEnum currency) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + userId));

        if (currency != null) {
            UserWalletEntity wallet = userWalletRepository.findByUserIdAndCurrency(user, currency)
                    .orElseThrow(() -> new IllegalArgumentException("Wallet not found for userId: " + userId));
            return walletTransactionRepository.findByUserWallet(wallet);
        }
        return walletTransactionRepository.findAllByUserId(user.getId());
    }
}