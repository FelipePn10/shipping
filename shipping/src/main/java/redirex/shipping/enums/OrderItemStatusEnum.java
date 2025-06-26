package redirex.shipping.enums;

public enum OrderItemStatusEnum {
        IN_CART,                    // No carrinho
        CREATING_ORDER,             // Pedido criado
        CANCELLED,
        PENDING_PAYMENT_PRODUCT,    // Aguardando pagamento do produto
        PAID,
        PAYMENT_FAILED,
        AWAITING_WAREHOUSE_ARRIVAL, // Aguardando chegada no armazém
        IN_WAREHOUSE,               // No armazém
        DELIVERED,

        PENDING_SHIPPING_PAYMENT,               // Aguardando pagamento do frete
        PROCESSING_IN_WAREHOUSE,                // Pagamento recebido, Em processamento no armazém (empacotamento, etc.)
        SHIPPED,                                // Enviado
        IN_TRANSIT,                             // Em trânsito

        ARRIVED_IN_YOUR_COUNTRY_OF_DESTINATION, // Chegou no país de destino

        HAS_ARRIVED_IN_YOUR_CITY,               // Chegou na cidade de destino

}
