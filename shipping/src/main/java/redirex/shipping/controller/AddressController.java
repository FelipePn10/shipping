package redirex.shipping.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.AddressDTO;
import redirex.shipping.dto.request.CreateAddressRequest;
import redirex.shipping.dto.response.AddressResponse;
import redirex.shipping.exception.AddressCreatedException;
import redirex.shipping.exception.AddressDeleteException;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.mapper.AddressMapper;
import redirex.shipping.service.AddressService;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class AddressController {
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    private final AddressService addressService;
    private final AddressMapper addressMapper;

    @PostMapping("/public/user/created-address")
    public ResponseEntity<?> createAddress(@Valid @RequestBody CreateAddressRequest request) {
        try {
            AddressDTO dto = addressMapper.toDTO(request);
            AddressResponse response = addressService.createdAddress(dto);
            return ResponseEntity.created(URI.create("/" + response.getId())).body(response);
        } catch (AddressCreatedException e) {
            logger.error("Address created error: {}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping("/public/user/update-address/{zipcode}")
    public ResponseEntity<?> updateAddress(@PathVariable String zipcode, @Valid @RequestBody AddressDTO dto) {
        try {
            logger.info("Received request to update address for zipcode: {}", zipcode);
            AddressResponse updateResponse = addressService.updateAddress(zipcode, dto);
            return ResponseEntity.ok(updateResponse);
        } catch (ResourceNotFoundException e) {
            logger.warn("Address not found for update: {}", zipcode);
            return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AddressCreatedException e) {
            logger.warn("Update failed: {}", e.getMessage());
            return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error updating address for zipcode {}: {}", zipcode, e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update address due to an unexpected error.");
        }
    }


    @DeleteMapping("/public/user/delete-address/{zipcode}")
    public ResponseEntity<?> deleteAddress(@PathVariable String zipcode) {
        try {
            logger.info("Received request to delete address for zipcode: {}", zipcode);

            AddressResponse deleteResponse = addressService.deleteAddress(zipcode);
            return ResponseEntity.ok(deleteResponse);
        } catch (ResourceNotFoundException e) {
            logger.warn("Address not found for update: {}", zipcode);
            return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AddressDeleteException e) {
            logger.warn("Delete failed: {}", e.getMessage());
            return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
        }  catch (Exception e) {
            logger.error("Unexpected error deleting address for zipcode {}: {}", zipcode, e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(message);
    }
}