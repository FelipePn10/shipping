package redirex.shipping.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.WarehouseEntity;
import redirex.shipping.enums.OrderItemStatusEnum;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.mapper.WarehouseMapper;
import redirex.shipping.repositories.OrderItemRepository;
import redirex.shipping.repositories.WarehouseRepository;
import redirex.shipping.dto.response.WarehouseResponse;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WarehouseService {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseService.class);

    private final WarehouseRepository warehouseRepository;
    private final OrderItemRepository orderItemRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseService(WarehouseRepository warehouseRepository, OrderItemRepository orderItemRepository, WarehouseMapper warehouseMapper) {
        this.warehouseRepository = warehouseRepository;
        this.orderItemRepository = orderItemRepository;
        this.warehouseMapper = warehouseMapper;
    }

    @Transactional
    public void addOrderItemToWarehouse(UUID orderItemId, UUID warehouseId) {
        logger.info("Adding order item {} to warehouse {}", orderItemId, warehouseId);

        OrderItemEntity orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        WarehouseEntity warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        // Atualiza status e relacionamento
        orderItem.setStatus(OrderItemStatusEnum.PAID);
        orderItem.setWarehouse(warehouse);
        orderItem.setArrivedAtWarehouseAt(LocalDateTime.now());

        // Adiciona à lista da warehouse
        warehouse.getOrderItems().add(orderItem);

        orderItemRepository.save(orderItem);
        warehouseRepository.save(warehouse);

        logger.info("Order item {} added to warehouse {}", orderItemId, warehouseId);
    }

    @Transactional(readOnly = true)
    public WarehouseResponse findWarehouseById(UUID id) {
        logger.info("Finding warehouse by ID: {}", id);
        WarehouseEntity warehouse = warehouseRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        return warehouseMapper.toResponse(warehouse);
    }

    @Transactional(readOnly = true)
    public WarehouseResponse findByUserId(UUID userId) {
        WarehouseEntity warehouse = warehouseRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found for user"));

        return warehouseMapper.toResponse(warehouse);
    }

    @Transactional
    public WarehouseEntity createWarehouseForUser(UserEntity user) {
        WarehouseEntity warehouse = WarehouseEntity.builder()
                .userId(user)
                .build();
        return warehouseRepository.save(warehouse);
    }
}