package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.AddressRequest;
import redirex.shipping.dto.request.CreateAddressRequest;
import redirex.shipping.dto.response.AddressResponse;
import redirex.shipping.exception.AddressCreatedException;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.mapper.AddressMapper;
import redirex.shipping.service.AddressService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/user")
public class AddressController {
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    private final AddressService addressService;
    private final AddressMapper addressMapper;

    public AddressController(AddressService addressService, AddressMapper addressMapper) {
        this.addressService = addressService;
        this.addressMapper = addressMapper;
    }

    @PostMapping("/create/address")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> createAddress(@Valid @RequestBody CreateAddressRequest request) {
        try {
            logger.info("Processing create address for authenticated user");
            AddressRequest dto = addressMapper.toDTO(request);
            AddressResponse response = addressService.createdAddress(dto);
            return ResponseEntity.created(URI.create("/" + response.getId())).body(response);
        } catch (AddressCreatedException e) {
            logger.error("Address creation error: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
        }
    }

    @PutMapping("/update/address/{zipcode}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> updateAddress(
            @PathVariable String zipcode,
            @Valid @RequestBody AddressRequest dto) {
        try {
            logger.info("Updating address for zipcode: {}", zipcode);
            AddressResponse updateResponse = addressService.updateAddress(zipcode, dto);
            return ResponseEntity.ok(updateResponse);
        } catch (ResourceNotFoundException e) {
            logger.warn("Address not found: {}", zipcode);
            return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("Update error for zipcode {}: {}", zipcode, e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
        }
    }

    @DeleteMapping("/delete/address/{zipcode}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> deleteAddress(@PathVariable String zipcode) {
        try {
            logger.info("Deleting address for zipcode: {}", zipcode);
            addressService.deleteAddress(zipcode);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            logger.warn("Address not found: {}", zipcode);
            return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("Delete error for zipcode {}: {}", zipcode, e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
        }
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(message);
    }
}