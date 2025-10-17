package redirex.shipping.mapper;

import java.math.BigDecimal;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import redirex.shipping.dto.request.CreateOrderItemRequest;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.entity.WarehouseEntity;
import redirex.shipping.enums.ProductCategoryEnum;
import redirex.shipping.enums.SizeEnum;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Arch Linux)"
)
@Component
public class OrderItemMapperImpl implements OrderItemMapper {

    @Override
    public CreateOrderItemRequest toDTO(OrderItemEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UUID userId = null;
        UUID warehouseId = null;
        String recipientCpf = null;
        String productUrl = null;
        SizeEnum size = null;
        String productName = null;
        String description = null;
        Integer quantity = null;
        BigDecimal productValue = null;

        userId = entityUserId( entity );
        warehouseId = entityWarehouseId( entity );
        recipientCpf = entity.getRecipientCpf();
        productUrl = entity.getProductUrl();
        size = entity.getSize();
        productName = entity.getProductName();
        description = entity.getDescription();
        quantity = entity.getQuantity();
        productValue = entity.getProductValue();

        ProductCategoryEnum category = null;
        boolean autoFetchPrice = false;

        CreateOrderItemRequest createOrderItemRequest = new CreateOrderItemRequest( userId, warehouseId, recipientCpf, productUrl, category, size, productName, description, quantity, productValue, autoFetchPrice );

        return createOrderItemRequest;
    }

    @Override
    public OrderItemEntity toEntity(OrderItemResponse dto) {
        if ( dto == null ) {
            return null;
        }

        OrderItemEntity.OrderItemEntityBuilder orderItemEntity = OrderItemEntity.builder();

        orderItemEntity.category( dto.category() );
        orderItemEntity.id( dto.id() );
        orderItemEntity.productUrl( dto.productUrl() );
        orderItemEntity.productName( dto.productName() );
        orderItemEntity.description( dto.description() );
        orderItemEntity.size( dto.size() );
        orderItemEntity.quantity( dto.quantity() );
        orderItemEntity.productValue( dto.productValue() );
        orderItemEntity.recipientCpf( dto.recipientCpf() );
        orderItemEntity.status( dto.status() );
        orderItemEntity.createdAt( dto.createdAt() );
        orderItemEntity.paymentDeadline( dto.paymentDeadline() );
        orderItemEntity.paidProductAt( dto.paidProductAt() );
        orderItemEntity.deliveredAt( dto.deliveredAt() );

        return orderItemEntity.build();
    }

    private UUID entityUserId(OrderItemEntity orderItemEntity) {
        UserEntity user = orderItemEntity.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private UUID entityWarehouseId(OrderItemEntity orderItemEntity) {
        WarehouseEntity warehouse = orderItemEntity.getWarehouse();
        if ( warehouse == null ) {
            return null;
        }
        return warehouse.getId();
    }
}
