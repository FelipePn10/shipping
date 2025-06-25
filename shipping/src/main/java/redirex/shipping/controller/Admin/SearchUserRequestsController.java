package redirex.shipping.controller.Admin;

import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/private/v1/api/admin")
@RequiredArgsConstructor
public class SearchUserRequestsController {
    private static final Logger logger = LoggerFactory.getLogger(SearchUserRequestsController.class);

    private final OrdersMadeByCustomersService ordersMadeByCustomersService;
    private JwtUtil  jwtUtil;

    @GetMapping("/{adminId}/search/orders")
    public ResponseEntity<Page<OrderItemResponse>> getOrdersAssignedToAdmin(
            @PathVariable Long adminId,
            Pageable pageable,
            Authentication authentication
    ) {

        Long authenticatedAdminId = jwtUtil.getAdminIdFromUsername(authentication.getName());

        if (!adminId.equals(authenticatedAdminId)) {
            return ResponseEntity.badRequest().build();
        }

        // Dados brutos
        Page<OrderItemEntity> orders = ordersMadeByCustomersService.findOrdersByAdminId(adminId, pageable);

        // Resposta limpa e formatada para o front-end
        Page<OrderItemResponse> ordersResponse = orders.map(this::toDto);

        return ResponseEntity.ok(ordersResponse);
    }
    private OrderItemResponse toDto(OrderItemEntity entity) {
        // Mapear campos desejados
        return OrderItemResponse.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .description(entity.getDescription())
                .productUrl(entity.getProductUrl())
                .quantity(entity.getQuantity())
                .size(entity.getSize())
                .build();
    }
}