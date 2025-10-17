package redirex.shipping.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final OrderItemPhotoRepository photoRepository;
    private final OrderItemStatusHistoryRepository statusHistoryRepository;
    // private final StorageService storageService; // TODO: Implementar serviço de upload

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<OrderItemEntity> getRecentOrders(Pageable pageable) {
        return orderItemRepository.findByStatusNotInOrderByCreatedAtDesc(
                List.of(OrderItemStatusEnum.IN_CART),
                pageable
        );
    }

    @Override
    public Page<OrderItemEntity> findOrdersByAdminId(UUID adminId, Pageable pageable) {
        return orderItemRepository.findByAdminAssignedId(adminId, pageable);
    }

    @Transactional
    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void updateOrderStatus(UUID orderId,
                                  OrderItemStatusEnum newStatus,
                                  AdminEntity admin,
                                  String notes,
                                  boolean isLocationUpdate) {

        OrderItemEntity order = orderItemRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Validação de admin
        if (order.getAdminAssigned() != null && !order.getAdminAssigned().getId().equals(admin.getId())) {
            throw new SecurityException(isLocationUpdate
                    ? "Admin not allowed to perform this operation! Consult your supervisor."
                    : "Admin not assigned to this request");
        }

        // Validação de transição
        if (isLocationUpdate) {
            validateLocationUpdate(order.getStatus(), newStatus);
        } else {
            validateStatusTransition(order.getStatus(), newStatus);
        }

        // Registra histórico
        registerStatusHistory(order, order.getStatus(), newStatus, admin, notes);

        // Atualiza status
        order.setStatus(newStatus);
        orderItemRepository.save(order);

        // Executa pós-processamento apenas para atualizações normais
        if (!isLocationUpdate) {
            handlePostStatusUpdate(order, newStatus);
        }
    }

    /**
     * Valida transições de status normais (fluxo de pedido)
     */
    private void validateStatusTransition(OrderItemStatusEnum current, OrderItemStatusEnum newStatus) {
        // Pedidos cancelados não podem ser reativados
        if (current == OrderItemStatusEnum.CANCELLED && newStatus != OrderItemStatusEnum.CANCELLED) {
            throw new IllegalStatusTransitionException("Pedidos cancelados não podem ser reativados");
        }

        // Validação de fluxo sequencial
        if (current == OrderItemStatusEnum.IN_CART && newStatus == OrderItemStatusEnum.IN_WAREHOUSE) {
            throw new IllegalStatusTransitionException("Transição inválida: pedido no carrinho não pode ir direto para armazém");
        }

        // Pedidos pagos não podem voltar para aguardando pagamento
        if (current == OrderItemStatusEnum.PAID && newStatus == OrderItemStatusEnum.PENDING_PAYMENT_PRODUCT) {
            throw new IllegalStatusTransitionException("Pedidos pagos não podem voltar para aguardando pagamento");
        }
    }

    /**
     * Valida atualizações de localização (usado para tracking)
     */
    private void validateLocationUpdate(OrderItemStatusEnum current, OrderItemStatusEnum newStatus) {
        // Apenas permite atualizações quando o pedido já foi pago
        if (current != OrderItemStatusEnum.PAID &&
                current != OrderItemStatusEnum.AWAITING_WAREHOUSE_ARRIVAL &&
                current != OrderItemStatusEnum.IN_WAREHOUSE) {
            throw new IllegalStatusTransitionException(
                    "Atualizações de localização só são permitidas para pedidos pagos ou em trânsito"
            );
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
                notifyCustomerAboutWarehouseArrival(order);
                break;
            case CANCELLED:
                // TODO: Processar reembolso se aplicável
                break;
        }
    }

    @Transactional
    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void addOrderPhoto(UUID orderId, MultipartFile file, String description, AdminEntity admin) {
        OrderItemEntity order = orderItemRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Valida se pode adicionar fotos
        if (!canAddPhotos(order.getStatus())) {
            throw new IllegalStateException(
                    "Fotos só podem ser adicionadas quando o pedido está no armazém ou aguardando chegada"
            );
        }

        // mais tarde será implementado upload de arquivo
        // String imageUrl = storageService.uploadFile(file);

        OrderItemPhotoEntity photo = OrderItemPhotoEntity.builder()
                // .imageUrl(imageUrl)
                .description(description)
                .orderItem(order)
                // .uploadedBy(admin)
                .build();

        photoRepository.save(photo);
    }

    private boolean canAddPhotos(OrderItemStatusEnum status) {
        return status == OrderItemStatusEnum.IN_WAREHOUSE ||
                status == OrderItemStatusEnum.AWAITING_WAREHOUSE_ARRIVAL;
    }

    private void notifyCustomerAboutWarehouseArrival(OrderItemEntity order) {
        // depois será feita uma implementação de notificação real
        System.out.println("Notificando cliente sobre chegada no armazém do pedido: " + order.getId());
    }
}