package redirex.shipping.enums;

public enum ShipmentEnum {
    PENDING_SHIPPING_PAYMENT,               // Aguardando pagamento do frete
    SHIPPING_PAID,                          // Pagamento do frete deu certo
    SHIPPING_PAID_FAILED,                   // Pagamento falhou
    SHIPPING_FAILED,                        // Envio falhou
    SHIPPED,                                // Enviado
    IN_TRANSIT,                             // Em trânsito
    ARRIVED_IN_YOUR_COUNTRY_OF_DESTINATION, // Chegou no país de destino
    HAS_ARRIVED_IN_YOUR_CITY,               // Chegou na cidade de destino
    DELIVERED,                              // Entregue
    CANCELLED
}
