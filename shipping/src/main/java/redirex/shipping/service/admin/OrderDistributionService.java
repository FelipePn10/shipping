package redirex.shipping.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redirex.shipping.entity.AdminEntity;
import redirex.shipping.repositories.AdminRepository;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderDistributionService {

    private final AdminRepository adminRepository;
    private final Random random = new Random();

    public AdminEntity assignRandomAdmin() {
        List<AdminEntity> activeAdmins = adminRepository.findActiveAdmins();

        if(activeAdmins.isEmpty()) {
            throw new IllegalStateException("No active admins available");
        }

        return activeAdmins.get(random.nextInt(activeAdmins.size()));
    }
}