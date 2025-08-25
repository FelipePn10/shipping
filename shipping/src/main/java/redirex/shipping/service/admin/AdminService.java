package redirex.shipping.service.admin;

import jakarta.validation.Valid;
import redirex.shipping.dto.request.RegisterAdminRequest;
import redirex.shipping.dto.request.UpdateAdminRequest;
import redirex.shipping.dto.response.RegisterAdminResponse;
import redirex.shipping.dto.response.UpdateAdminResponse;

import java.util.UUID;

public interface AdminService {
    RegisterAdminResponse createAdmin(@Valid RegisterAdminRequest dto);
    UpdateAdminResponse updateAdmin(UUID id, @Valid UpdateAdminRequest dto);

    UUID findAdminIdByEmail(String email);
}
