package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.dto.request.AddressRequest;
import redirex.shipping.dto.response.AddressResponse;

public interface AddressService {
    AddressResponse createdAddress(@Valid AddressRequest dto);
    AddressResponse updateAddress(String zipcode, @Valid AddressRequest dto);
    void deleteAddress(String zipcode);
}