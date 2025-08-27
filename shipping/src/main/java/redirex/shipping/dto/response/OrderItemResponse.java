package redirex.shipping.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import redirex.shipping.enums.OrderItemStatusEnum;
import redirex.shipping.enums.ProductCategoryEnum;
import redirex.shipping.enums.SizeEnum;
import redirex.shipping.util.CpfMaskSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OrderItemResponse {
    private UUID id;
    private UUID userId;
    private UUID warehouseId;
    @JsonSerialize(using = CpfMaskSerializer.class)
    private String recipientCpf;
    private String productUrl;
    private String productName;
    private String description;
    private SizeEnum size;
    private ProductCategoryEnum category;
    private Integer quantity;
    private BigDecimal productValue;
    private OrderItemStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime paidProductAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime paymentDeadline;
}
