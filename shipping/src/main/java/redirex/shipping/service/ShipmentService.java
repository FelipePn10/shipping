package redirex.shipping.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.entity.ShipmentEntity;
import redirex.shipping.entity.UserCouponEntity;
import redirex.shipping.repositories.ShipmentRepository;
import redirex.shipping.repositories.UserCouponRepository;

import java.util.UUID;

@Service
public class ShipmentService {
    private ShipmentRepository shipmentRepository;
    private UserCouponRepository userCouponRepository;

    @Transactional
    public void applyCouponToShipment(UUID shipmentId, UUID userCouponId) {
        ShipmentEntity shipmentEntity = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("ShipmentEntity not found"));
        UserCouponEntity userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("UserCoupon not found"));

        shipmentEntity.applyCoupon(userCoupon);
        shipmentRepository.save(shipmentEntity);
        userCouponRepository.save(userCoupon);
    }
}
