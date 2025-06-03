package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.dto.RegisterAdminDTO;
import redirex.shipping.dto.response.AdminResponse;

public interface AdminService {
    AdminResponse createAdmin(@Valid RegisterAdminDTO dto);
    AdminResponse updateAdmin(Long id, @Valid RegisterAdminDTO dto);


}
