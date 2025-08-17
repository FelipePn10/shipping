package redirex.shipping.service.admin;

import jakarta.validation.Valid;
import redirex.shipping.dto.RegisterAdminDTO;
import redirex.shipping.dto.response.AdminResponse;

import java.util.UUID;

public interface AdminService {
    AdminResponse createAdmin(@Valid RegisterAdminDTO dto);
    AdminResponse updateAdmin(UUID id, @Valid RegisterAdminDTO dto);

    UUID findAdminIdByEmail(String email);
}
