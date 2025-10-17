package redirex.shipping.enums;

public enum OrderItemStatusEnum {
        IN_CART,                    // No carrinho
        CREATING_ORDER,             // Pedido criado
        CANCELLED,                  // Cancelado
        PENDING_PAYMENT_PRODUCT,    // Aguardando pagamento do produto
        PAID,                       // Pago
        PAYMENT_FAILED,             // Falha no pagamento
        AWAITING_WAREHOUSE_ARRIVAL, // Aguardando chegada no armazém
        IN_WAREHOUSE                // No armazém
}
