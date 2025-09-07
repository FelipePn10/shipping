package redirex.shipping.controller.Admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redirex.shipping.dto.response.OrderItemResponse;
import redirex.shipping.entity.OrderItemEntity;
import redirex.shipping.security.JwtUtil;
import redirex.shipping.service.admin.OrdersMadeByCustomersService;

import java.util.UUID;

@RestController
@RequestMapping("/private/api/v1/admin")
public class SearchUserRequestsController {
    private static final Logger logger = LoggerFactory.getLogger(SearchUserRequestsController.class);

    private final OrdersMadeByCustomersService ordersMadeByCustomersService;
    private final JwtUtil jwtUtil;

    public SearchUserRequestsController(OrdersMadeByCustomersService ordersMadeByCustomersService, JwtUtil jwtUtil) {
        this.ordersMadeByCustomersService = ordersMadeByCustomersService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/{adminId}/search/orders")
    public ResponseEntity<Page<OrderItemResponse>> getOrdersAssignedToAdmin(
            @PathVariable UUID adminId,
            Pageable pageable,
            Authentication authentication
    ) {
        UUID authenticatedAdminId = jwtUtil.getAdminIdFromUsername(authentication.getName());

        if (!adminId.equals(authenticatedAdminId)) {
            return ResponseEntity.badRequest().build();
        }

        Page<OrderItemEntity> orders = ordersMadeByCustomersService.findOrdersByAdminId(adminId, pageable);
        Page<OrderItemResponse> ordersResponse = orders.map(this::toDto);

        return ResponseEntity.ok(ordersResponse);
    }

    private OrderItemResponse toDto(OrderItemEntity entity) {
        return new OrderItemResponse(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getWarehouse() != null ? entity.getWarehouse().getId() : null,
                entity.getRecipientCpf(),
                entity.getProductUrl(),
                entity.getProductName(),
                entity.getDescription(),
                entity.getSize(),
                entity.getCategory(),
                entity.getQuantity(),
                entity.getProductValue(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getPaidProductAt(),
                entity.getDeliveredAt(),
                entity.getPaymentDeadline()
        );
    }
}