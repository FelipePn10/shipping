package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.controller.dto.response.AddressResponse;
import redirex.shipping.dto.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressResponse createdAddress(@Valid AddressDTO dto);
    AddressResponse updateAddress(String zipcode, @Valid AddressDTO dto);
    AddressResponse deleteAddress(Long id);
//    AddressResponse getAddressById(Long id);
//    List<AddressResponse> listAllAddresses();
}