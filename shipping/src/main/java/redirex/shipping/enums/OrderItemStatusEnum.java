package redirex.shipping.enums;

public enum OrderItemStatusEnum {
        CREATING_ORDER,
        IN_CART,                    // No carrinho
        PENDING_PAYMENT_PRODUCT,    // Aguardando pagamento do produto
        AWAITING_WAREHOUSE_ARRIVAL, // Aguardando chegada no armazém
        IN_WAREHOUSE,               // No armazém
        PENDING_SHIPPING_PAYMENT,   // Aguardando pagamento do frete
        PROCESSING_IN_WAREHOUSE,    // Em processamento no armazém (empacotamento, etc.)
        SHIPPED,                    // Enviado
        DELIVERED,                  // Entregue
        CANCELLED,
        PAID,
        PAYMENT_FAILED
    }
