package redirex.shipping.service.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.repositories.AdminRepository;
import redirex.shipping.repositories.OrderItemRepository;
import redirex.shipping.enums.OrderItemStatusEnum;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDistributionService {

    private final AdminRepository adminRepository;
    private final OrderItemRepository orderItemRepository;
    private final Random random = new Random();

    //Considerados como pedidos ativos para o admin gerir
    private static final List<OrderItemStatusEnum> ACTIVE_STATUSES = List.of(
            OrderItemStatusEnum.PAID,
            OrderItemStatusEnum.PENDING_PAYMENT_PRODUCT,
            OrderItemStatusEnum.AWAITING_WAREHOUSE_ARRIVAL,
            OrderItemStatusEnum.IN_WAREHOUSE
    );

    public AdminEntity assignToLeastBusyAdmin() {
        List<AdminEntity> allAdmins = adminRepository.findByRole("ADMIN");

        if(allAdmins.isEmpty()) {
            throw new IllegalStateException("No administrators available");
        }

        // Se só há 1 admin, retorna direto
        if(allAdmins.size() == 1) {
            return allAdmins.get(0);
        }

        // Calcula carga de trabalho para cada admin
        List<AdminWorkload> workloads = allAdmins.stream()
                .map(admin -> new AdminWorkload(
                        admin,
                        orderItemRepository.countByAdminAssignedAndStatusIn(admin, ACTIVE_STATUSES)
                ))
                .collect(Collectors.toList());

        // Encontra a menor carga de trabalho
        long minWorkload = workloads.stream()
                .mapToLong(AdminWorkload::getActiveOrders)
                .min()
                .orElse(0);

        // Filtra admins com a menor carga
        List<AdminEntity> leastBusyAdmins = workloads.stream()
                .filter(w -> w.getActiveOrders() == minWorkload)
                .map(AdminWorkload::getAdmin)
                .collect(Collectors.toList());

        // Escolhe aleatoriamente entre os menos ocupados
        return leastBusyAdmins.get(random.nextInt(leastBusyAdmins.size()));
    }

    // Classe auxiliar para armazenar carga de trabalho
    @Getter
    @AllArgsConstructor
    private static class AdminWorkload {
        private final AdminEntity admin;
        private final long activeOrders;
    }
}