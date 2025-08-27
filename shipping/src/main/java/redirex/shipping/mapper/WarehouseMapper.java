package redirex.shipping.mapper;

import org.springframework.stereotype.Component;
import redirex.shipping.dto.response.OrderItemSummaryResponse;
import redirex.shipping.dto.response.UserCouponResponse;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.WarehouseEntity;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WarehouseMapper {

    public UserCouponResponse.WarehouseResponse toResponse(WarehouseEntity entity) {
        // Ordena os itens por data de chegada (mais recente primeiro)
        List<OrderItemSummaryResponse> items = entity.getOrderItems().stream()
                .sorted(Comparator.comparing(OrderItemEntity::getArrivedAtWarehouseAt).reversed())
                .map(this::toOrderItemSummary)
                .collect(Collectors.toList());

        return UserCouponResponse.WarehouseResponse.builder()
                .id(entity.getId())
                .orderItems(items)
                .build();
    }

    private OrderItemSummaryResponse toOrderItemSummary(OrderItemEntity item) {
        return OrderItemSummaryResponse.builder()
                .id(item.getId())
                .description(item.getDescription())
                .status(item.getStatus())
                .arrivedAt(item.getArrivedAtWarehouseAt())
                .build();
    }
}