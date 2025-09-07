package redirex.shipping.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import redirex.shipping.enums.OrderItemStatusEnum;
import redirex.shipping.enums.ProductCategoryEnum;
import redirex.shipping.enums.SizeEnum;
import redirex.shipping.util.CpfMaskSerializer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderItemResponse (
    UUID id,
    UUID userId,
    UUID warehouseId,
    @JsonSerialize(using = CpfMaskSerializer.class)
    String recipientCpf,
    String productUrl,
    String productName,
    String description,
    SizeEnum size,
    ProductCategoryEnum category,
    Integer quantity,
    BigDecimal productValue,
    OrderItemStatusEnum status,
    LocalDateTime createdAt,
    LocalDateTime paidProductAt,
    LocalDateTime deliveredAt,
    LocalDateTime paymentDeadline
) {

}
