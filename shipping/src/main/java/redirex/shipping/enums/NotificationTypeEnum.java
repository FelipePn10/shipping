package redirex.shipping.enums;

public enum NotificationTypeEnum {
    ORDER_STATUS,     // Atualizações sobre pedidos (OrderItemEntity)
    WALLET_UPDATE,    // Atualizações sobre carteira (UserWallet)
    PROMOTION,        // Mensagens promocionais (ex.: cupons)
    ADMIN_MESSAGE     // Mensagens gerais do administrador
}
