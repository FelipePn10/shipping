package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.dto.RegisterAdminDTO;
import redirex.shipping.dto.response.AdminResponse;

public interface AdminService {
    AdminResponse createAdmin(@Valid RegisterAdminDTO dto);
    AdminResponse updateAdmin(@Valid RegisterAdminDTO dto);

    AdminResponse findAdminById(Long id);
    Long findAdminByEmail(String email);
}
