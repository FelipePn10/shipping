package redirex.shipping.service;

import lombok.RequiredArgsConstructor;
import redirex.shipping.entity.AddressEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.repositories.AddressRepository;
import redirex.shipping.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddressEntity createAddress(Long userId, AddressEntity address) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        address.setUser(user);
        return addressRepository.save(address);
    }

    @Transactional
    public AddressEntity updateAddress(Long addressId, AddressEntity updatedAddress) {
        AddressEntity existing = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        // Atualiza apenas os campos permitidos
        existing.setRecipientName(updatedAddress.getRecipientName());
        existing.setStreet(updatedAddress.getStreet());
        existing.setComplement(updatedAddress.getComplement());
        existing.setCity(updatedAddress.getCity());
        existing.setState(updatedAddress.getState());
        existing.setZipcode(updatedAddress.getZipcode());
        existing.setCountry(updatedAddress.getCountry());
        existing.setResidenceType(updatedAddress.getResidenceType());

        return addressRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public List<AddressEntity> getAddressesByUser(Long userId) {
        return addressRepository.findByUserId(userId);
    }
}