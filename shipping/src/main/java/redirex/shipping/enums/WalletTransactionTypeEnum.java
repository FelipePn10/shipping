package redirex.shipping.enums;

public enum WalletTransactionTypeEnum {
    DEPOSIT,                    // Depósito na carteira
    WITHDRAWAL_PRODUCT_PAYMENT, // Saque para pagamento de produto (OrderItem)
    WITHDRAWAL_SHIPPING_PAYMENT,// Saque para pagamento de envio (ShipmentEntity)
    REFUND                      // Reembolso
}