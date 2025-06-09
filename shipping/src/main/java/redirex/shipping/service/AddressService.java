package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.dto.AddressDTO;
import redirex.shipping.dto.response.AddressResponse;

public interface AddressService {
    AddressResponse createdAddress(@Valid AddressDTO dto);
    AddressResponse updateAddress(String zipcode, @Valid AddressDTO dto);
    AddressResponse deleteAddress(String zipcode);
}