package redirex.shipping.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import redirex.shipping.dto.RegisterAdminDTO;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @PostMapping("/private/admin/v1/create/admin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody RegisterAdminDTO dto) {
        try {

        }
    }

}
