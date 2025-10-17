package redirex.shipping.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.dto.request.AddShipmentToOrderRequest;
import redirex.shipping.dto.request.PaymentShipmentRequest;
import redirex.shipping.dto.response.AddShipmentToOrderResponse;
import redirex.shipping.dto.response.PaymentShipmentResponse;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.ShipmentEntity;
import redirex.shipping.entity.UserCouponEntity;
import redirex.shipping.enums.ShipmentEnum;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.repositories.OrderItemRepository;
import redirex.shipping.repositories.ShipmentRepository;
import redirex.shipping.repositories.UserCouponRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserCouponRepository userCouponRepository;

    @Override
    @Transactional
    public AddShipmentToOrderResponse addShipmentToOrder(UUID orderId, @Valid AddShipmentToOrderRequest request) {
        log.info("Adding shipment to order: {}", orderId);

        OrderItemEntity orderItem = orderItemRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Mais tarde irei implementar lógica de criação de shipment
        // Validar se o pedido está no status correto (IN_WAREHOUSE)
        // Criar entidade ShipmentEntity
        // Associar orderItem ao shipment
        // Calcular custos de frete

        log.info("Shipment added successfully for order: {}", orderId);
        return new AddShipmentToOrderResponse();
    }

    @Override
    @Transactional
    public PaymentShipmentResponse processOrderPayment(UUID orderId, @Valid PaymentShipmentRequest request) {
        log.info("Processing shipment payment for order: {}", orderId);

        // Tambem irei mais tarde implementar lógica de pagamento
        // Buscar shipment associado ao order
        // Validar saldo da carteira do usuário
        // Processar débito na carteira
        // Atualizar status do shipment para SHIPPING_PAID
        // Criar transação de wallet

        log.info("Shipment payment processed successfully for order: {}", orderId);
        return new PaymentShipmentResponse();
    }

    @Override
    @Transactional
    public void applyCouponToShipment(UUID shipmentId, UUID userCouponId) {
        log.info("Applying coupon {} to shipment {}", userCouponId, shipmentId);

        ShipmentEntity shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + shipmentId));

        UserCouponEntity userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new ResourceNotFoundException("User coupon not found with id: " + userCouponId));

        // Validações
        if (shipment.getAppliedShippingCoupon() != null) {
            throw new IllegalStateException("Shipment already has a coupon applied");
        }

        if (shipment.getStatus() != ShipmentEnum.PENDING_SHIPPING_PAYMENT) {
            throw new IllegalStateException("Coupon can only be applied to shipments pending payment");
        }

        // Aplicar cupom usando o método da entidade
        shipment.applyCoupon(userCoupon);
        shipmentRepository.save(shipment);

        log.info("Coupon applied successfully to shipment: {}", shipmentId);
    }
}