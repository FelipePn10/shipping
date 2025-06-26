package redirex.shipping.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.enums.OrderItemStatusEnum;

public interface OrdersMadeByCustomersService {

    Page<OrderItemEntity> getRecentOrders(Pageable pageable);
    Page<OrderItemEntity> findOrdersByAdminId(Long adminId, Pageable pageable);

    @Transactional
    void updateOrderStatus(Long orderId, OrderItemStatusEnum newStatusCurrent, AdminEntity admin, String notes, boolean isLocationUpdate);

    void addOrderPhoto(Long orderId, MultipartFile file, String description, AdminEntity admin);
}