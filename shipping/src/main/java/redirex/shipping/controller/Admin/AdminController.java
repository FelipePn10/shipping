package redirex.shipping.controller.Admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.RegisterAdminRequest;
import redirex.shipping.dto.request.UpdateAdminRequest;
import redirex.shipping.dto.response.RegisterAdminResponse;
import redirex.shipping.dto.response.UpdateAdminResponse;
import redirex.shipping.service.admin.AdminService;

import java.util.UUID;

@RestController
@RequestMapping("/private/v1")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/create/admin")
    public ResponseEntity<RegisterAdminResponse> createAdmin(@Valid @RequestBody RegisterAdminRequest dto) {
        log.info("Received request to create admin: {}", dto.getEmail());
        RegisterAdminResponse adminResponse = adminService.createAdmin(dto);
        log.info("Admin created successfully: {}", adminResponse.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(adminResponse);
    }

    @PutMapping("admin/{id}")
    public ResponseEntity<UpdateAdminResponse> updateAdmin(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAdminRequest dto) {
        log.info("Received request to update admin: {}", id);
        UpdateAdminResponse adminResponse = adminService.updateAdmin(id, dto);
        log.info("Admin updated successfully: {}", adminResponse.getEmail());
        return ResponseEntity.ok(adminResponse);
    }
}

