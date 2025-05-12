package redirex.shipping.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.entity.Shipment;
import redirex.shipping.entity.UserCouponEntity;
import redirex.shipping.repositories.ShipmentRepository;
import redirex.shipping.repositories.UserCouponRepository;

@Service
public class ShipmentService {
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;

    @Transactional
    public void applyCouponToShipment(Long shipmentId, Long userCouponId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));
        UserCouponEntity userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("UserCoupon not found"));

        shipment.applyCoupon(userCoupon);
        shipmentRepository.save(shipment);
        userCouponRepository.save(userCoupon);
    }
}
