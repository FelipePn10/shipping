package redirex.shipping.enums;

public enum ShipmentStatusEnum {
    PENDING_PAYMENT, // Aguardando pagamento do frete
    PROCESSING,      // Pagamento recebido, processando (empacotando, etc.)
    SHIPPED,         // Enviado
    IN_TRANSIT,      // Em trânsito
    DELIVERED,       // Entregue
    CANCELLED        // Cancelado
}
