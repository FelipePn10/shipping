package redirex.shipping.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redirex.shipping.controller.dto.response.WarehouseResponse;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.WarehouseEntity;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.mapper.WarehouseMapper;
import redirex.shipping.repositories.OrderItemRepository;
import redirex.shipping.repositories.WarehouseRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseService.class);

    private final WarehouseRepository warehouseRepository;
    private final OrderItemRepository orderItemRepository;
    private final WarehouseMapper warehouseMapper;

    @Transactional
    public void assignOrderItemToWarehouse(Long orderItemId, Long warehouseId) {
        logger.info("Assigning order item {} to warehouse {}", orderItemId, warehouseId);

        OrderItemEntity orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item with ID " + orderItemId + " not found"));
        WarehouseEntity warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse with ID " + warehouseId + " not found"));

        orderItem.setWarehouse(warehouse);
        orderItem.setArrivedAtWarehouseAt(LocalDateTime.now());
        orderItemRepository.save(orderItem);

        logger.info("Order item {} assigned to warehouse {}", orderItemId, warehouseId);
    }

    @Transactional(readOnly = true)
    public WarehouseResponse findWarehouseById(Long id) {
        logger.info("Finding warehouse by ID: {}", id);
        WarehouseEntity warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse with ID " + id + " not found"));
        return warehouseMapper.toResponse(warehouse);
    }
}