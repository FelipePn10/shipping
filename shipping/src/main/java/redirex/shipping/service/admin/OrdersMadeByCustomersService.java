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
    void updateOrderStatus(Long orderId, OrderItemStatusEnum newStatus, AdminEntity admin);

    @Transactional
    void updateOrderStatus(Long orderId, OrderItemStatusEnum newStatus, AdminEntity admin, String notes);

    void addOrderPhoto(Long orderId, MultipartFile file, String description, AdminEntity admin);
}