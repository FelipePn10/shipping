package redirex.shipping.service;

import jakarta.validation.Valid;
import redirex.shipping.dto.response.AddressResponse;
import redirex.shipping.dto.AddressDTO;

public interface AddressService {
    AddressResponse createdAddress(@Valid AddressDTO dto);
    AddressResponse updateAddress(String zipcode, @Valid AddressDTO dto);
    AddressResponse deleteAddress(Long id);
//    AddressResponse getAddressById(Long walletId);
//    List<AddressResponse> listAllAddresses();
}