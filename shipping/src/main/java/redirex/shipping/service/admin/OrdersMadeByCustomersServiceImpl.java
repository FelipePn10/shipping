package redirex.shipping.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.OrderItemPhotoEntity;
import redirex.shipping.entity.OrderItemStatusHistoryEntity;
import redirex.shipping.enums.OrderItemStatusEnum;
import redirex.shipping.exception.IllegalStatusTransitionException;
import redirex.shipping.repositories.OrderItemPhotoRepository;
import redirex.shipping.repositories.OrderItemRepository;
import redirex.shipping.repositories.OrderItemStatusHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrdersMadeByCustomersServiceImpl implements OrdersMadeByCustomersService {

    private final OrderItemRepository orderItemRepository;
    // private final StorageSerice storageService - service para upload de photos
    private final OrderItemPhotoRepository photoRepository;
    private final OrderItemStatusHistoryRepository statusHistoryRepository;

    @Override
    public Page<OrderItemEntity> getRecentOrders(Pageable pageable) {
        // Busca pedidos excluindo status iniciais e ordena por criação decrescente
        return orderItemRepository.findByStatusNotInOrderByCreatedAtDesc(
                List.of(OrderItemStatusEnum.IN_CART),
                pageable
        );
    }

    public Page<OrderItemEntity> findOrdersByAdminId(UUID adminId, Pageable pageable) {
        return orderItemRepository.findByAdminAssignedId(adminId, pageable);
    }

    @Transactional
    @Override
    public void updateOrderStatus(UUID orderId,
                                  OrderItemStatusEnum newStatus,
                                  AdminEntity admin,
                                  String notes,
                                  boolean isLocationUpdate) { // Flag para identificar tipo de operação

        OrderItemEntity order = orderItemRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Validação de admin
        if (!order.getAdminAssigned().getId().equals(admin.getId())) {
            throw new SecurityException(isLocationUpdate
                    ? "Admin not allowed to perform this operation! Consult your supervisor."
                    : "Admin not assigned to this request");
        }

        // Validação de transição
        if (isLocationUpdate) {
            validateShippingTransition(order.getStatus(), newStatus);
        } else {
            validateStatusTransition(order.getStatus(), newStatus);
        }

        registerStatusHistory(order, order.getStatus(), newStatus, admin, notes);

        order.setStatus(newStatus);
        orderItemRepository.save(order);

        // Executa pós-processamento apenas para atualizações normais
        if (!isLocationUpdate) {
            handlePostStatusUpdate(order, newStatus);
        }
    }

    private void validateStatusTransition(OrderItemStatusEnum current, OrderItemStatusEnum newStatus) {
        // Lógica de validação de transições permitidas
        if (current == OrderItemStatusEnum.CANCELLED && newStatus != OrderItemStatusEnum.CANCELLED) {
            throw new IllegalStatusTransitionException("Pedidos cancelados não podem ser reativados");
        }

        if (current == OrderItemStatusEnum.DELIVERED && newStatus != OrderItemStatusEnum.DELIVERED) {
            throw new IllegalStatusTransitionException("Pedidos entregues não podem ser alterados");
        }
    }

    private void validateShippingTransition(OrderItemStatusEnum statusShipping, OrderItemStatusEnum newStatus) {
        if (statusShipping == OrderItemStatusEnum.DELIVERED && newStatus != OrderItemStatusEnum.DELIVERED) {
            throw new IllegalStatusTransitionException("Pedidos entregues não podem ser alterados");
        }

        if (statusShipping == OrderItemStatusEnum.SHIPPED && newStatus != OrderItemStatusEnum.SHIPPED) {
            throw new IllegalStatusTransitionException("Pedidos enviados não podem ser alterados");
        }
    }

    private void registerStatusHistory(OrderItemEntity order,
                                       OrderItemStatusEnum oldStatus,
                                       OrderItemStatusEnum newStatus,
                                       AdminEntity admin,
                                       String notes) {

        OrderItemStatusHistoryEntity history = OrderItemStatusHistoryEntity.builder()
                .orderItem(order)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(admin)
                .notes(notes)
                .build();

        statusHistoryRepository.save(history);
    }

    private void handlePostStatusUpdate(OrderItemEntity order, OrderItemStatusEnum newStatus) {
        switch (newStatus) {
            case PAID:
                order.setPaidProductAt(LocalDateTime.now());
                break;
            case IN_WAREHOUSE:
                order.setArrivedAtWarehouseAt(LocalDateTime.now());
                break;
        }

        // Pode adicionar notificações aqui
        if (newStatus == OrderItemStatusEnum.IN_WAREHOUSE) {
            notifyCustomerAboutShipment(order);
        }
    }

    @Transactional
    @Override
    public void addOrderPhoto(UUID orderId, MultipartFile file, String description, AdminEntity admin) {
        OrderItemEntity order = orderItemRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

       // String imageUrl = storageService.uploadFile(file);

        OrderItemPhotoEntity photo = OrderItemPhotoEntity.builder()
                //.imageUrl(imageUrl)
                .description(description)
                .orderItem(order)
                //.uploadedAt(admin)
                .build();

        //photoRepository.save(photo);
    }

    private boolean canAddPhotos(OrderItemStatusEnum status) {
        // Só permite adicionar fotos em estados específicos
        return status == OrderItemStatusEnum.IN_WAREHOUSE ||
                status == OrderItemStatusEnum.AWAITING_WAREHOUSE_ARRIVAL;
    }

    // Metodo auxiliar para notificação (implementação fictícia por enquanto)
    private void notifyCustomerAboutShipment(OrderItemEntity order) {
        // Lógica para enviar e-mail/notificação ao cliente
        System.out.println("Notificando cliente sobre envio do pedido: " + order.getId());
    }
}
