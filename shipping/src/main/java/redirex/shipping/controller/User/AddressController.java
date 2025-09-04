package redirex.shipping.controller.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redirex.shipping.dto.request.AddressRequest;
import redirex.shipping.dto.request.CreateAddressRequest;
import redirex.shipping.dto.response.AddressResponse;
import redirex.shipping.dto.response.ApiErrorResponse;
import redirex.shipping.dto.response.ApiResponse;
import redirex.shipping.exception.AddressCreatedException;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.mapper.AddressMapper;
import redirex.shipping.service.AddressService;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

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

    @PostMapping("{userId}/create/address")
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateAddressRequest request) {
        try {
            if (!Objects.equals(userId, request.getUserId())) {
                logger.warn("UserId inconsistency: pathUserId={}, requestUserId={} ", userId, request.getUserId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .build();
            }

            logger.info("Processing create address for authenticated user");
            AddressRequest dto = addressMapper.toDTO(request);
            logger.info("Received request to create address for authenticated user");
            AddressResponse response = addressService.createdAddress(dto);
            logger.info("Address created for authenticated user: {}", response);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<AddressResponse>builder()
                            .data(response)
                            .timestamp(LocalDateTime.now())
                            .build());
        } catch (AddressCreatedException e) {
            logger.error("Address creation error: {}", e.getMessage(), e);
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Error registering user. Reason: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<AddressResponse>builder().error(error).build());
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage(), e);
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Error internal server. Reason: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<AddressResponse>builder().error(error).build());
        }
    }

    @PutMapping("/update/address/{zipcode}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable String zipcode,
            @Valid @RequestBody AddressRequest dto) {
        try {
            logger.info("Updating address for zipcode: {}", zipcode);
            AddressResponse updateResponse = addressService.updateAddress(zipcode, dto);
            logger.info("Address updated for zipcode: {}", updateResponse);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<AddressResponse>builder()
                            .data(updateResponse)
                            .timestamp(LocalDateTime.now())
                            .build());
        } catch (ResourceNotFoundException e) {
            logger.warn("address selected to update is not registered in the database: {}", zipcode);
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Address not found. Reason: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<AddressResponse>builder().error(error).build());
        } catch (Exception e) {
            logger.error("Update error for zipcode {}: {}", zipcode, e.getMessage(), e);
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error internal server. Reason: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<AddressResponse>builder().error(error).build());
        }
    }

    @DeleteMapping("/delete/address/{zipcode}")
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