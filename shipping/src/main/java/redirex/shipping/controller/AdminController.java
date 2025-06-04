package redirex.shipping.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import redirex.shipping.dto.RegisterAdminDTO;
import redirex.shipping.dto.response.AdminResponse;
import redirex.shipping.exception.AdminRegistrationException;
import redirex.shipping.exception.AdminUpdateException;
import redirex.shipping.service.AdminService;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;


    @PostMapping("/public/admin/v1/create/admin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody RegisterAdminDTO dto) {
        try {
            logger.info("Received request to create admin: {}", dto.getEmail());
            AdminResponse adminResponse = adminService.createAdmin(dto);
            logger.info("Admin created successfully: {}", adminResponse.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(adminResponse);
        } catch (AdminRegistrationException e) {
            logger.info("Admin registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.info("Server error. Please try again later. Sorry for the inconvenience. : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/private/admin/v1/update/admin")
    public ResponseEntity<?> updateAdmin(Long id, @Valid @RequestBody RegisterAdminDTO dto) {
        try {
            logger.info("Received request to update admin: {}", dto.getEmail());
            AdminResponse adminResponse = adminService.updateAdmin(id, dto);
            return ResponseEntity.status(HttpStatus.OK).body(adminResponse);
        } catch (AdminUpdateException e) {
            logger.info("Admin update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.info("Admin update failed! Server error. Please try again later. Sorry for the inconvenience.: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
