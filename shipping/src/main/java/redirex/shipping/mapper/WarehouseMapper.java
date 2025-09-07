package redirex.shipping.mapper;

import org.springframework.stereotype.Component;
import redirex.shipping.dto.response.OrderItemSummaryResponse;
import redirex.shipping.dto.response.WarehouseResponse;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.WarehouseEntity;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WarehouseMapper {

    public WarehouseResponse toResponse(WarehouseEntity entity) {
        if (entity == null) {
            return null;
        }

        // Ordena os itens por data de chegada (mais recente primeiro)
        List<OrderItemSummaryResponse> items = entity.getOrderItems().stream()
                .sorted(Comparator.comparing(OrderItemEntity::getArrivedAtWarehouseAt).reversed())
                .map(this::toOrderItemSummary)
                .collect(Collectors.toList());

        return new WarehouseResponse(
                entity.getId(),
                items
        );
    }

    private OrderItemSummaryResponse toOrderItemSummary(OrderItemEntity item) {
        if (item == null) {
            return null;
        }

        return new OrderItemSummaryResponse(
                item.getId(),
                item.getDescription(),
                item.getStatus(),
                item.getArrivedAtWarehouseAt()
        );
    }
}