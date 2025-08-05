package redirex.shipping.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import redirex.shipping.dto.AddressDTO;
import redirex.shipping.dto.response.AddressResponse;
import redirex.shipping.entity.AddressEntity;
import redirex.shipping.entity.UserEntity;
import redirex.shipping.exception.AddressCreatedException;
import redirex.shipping.exception.ResourceNotFoundException;
import redirex.shipping.mapper.AddressMapper;
import redirex.shipping.repositories.AddressRepository;
import redirex.shipping.repositories.UserRepository;

import java.time.LocalDateTime;

@Service

public class AddressServiceImpl implements AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, AddressMapper addressMapper, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AddressResponse createdAddress(@Valid AddressDTO dto) {
        logger.info("Create address request: {}", dto.getZipcode());
        validateAddressDoesNotExist(dto.getZipcode());

        if (dto.getUserId() == null) {
            logger.error("User ID is null in AddressDTO");
            throw new IllegalArgumentException("User ID cannot be null");
        }

        try {
            UserEntity user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User with ID " + dto.getUserId() + " not found"));
            AddressEntity address = addressMapper.toEntity(dto);
            address.setCreatedAt(LocalDateTime.now());
            address.setUser(user);
            addressRepository.save(address);

            logger.info("Address successfully created");
            return addressMapper.toResponse(address);
        } catch (Exception e) {
            logger.error("Address creation failed: {}", e.getMessage());
            throw new AddressCreatedException("Failed to create address", e);
        }
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(String zipcode, @Valid AddressDTO dto) {
        logger.info("Update address request: {}", zipcode);
        AddressEntity address = addressRepository.findByZipcode(zipcode)
                .orElseThrow(() -> new ResourceNotFoundException("Address with zipcode " + zipcode + " not found"));

        // Verifica se o novo CEP é diferente e já existe
        if (!dto.getZipcode().equals(zipcode)) {
            validateAddressDoesNotExist(dto.getZipcode());
        }

        address.setRecipientName(dto.getRecipientName());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setStreet(dto.getStreet());
        address.setCountry(dto.getCountry());
        address.setPhone(dto.getPhone());
        address.setResidenceType(dto.getResidenceType());
        address.setComplement(dto.getComplement());

        // Atualiza ZIPCODE separadamente com tratamento de concorrência
        if (!dto.getZipcode().equals(zipcode)) {
            address.setZipcode(dto.getZipcode());
        }

        try {
            AddressEntity updatedAddress = addressRepository.save(address);
            logger.info("Address successfully updated");
            return addressMapper.toResponse(updatedAddress);
        } catch (DataIntegrityViolationException e) {
            throw new AddressCreatedException("Zipcode " + dto.getZipcode() + " already exists");
        }
    }

    @Override
    public AddressResponse deleteAddress(String zipcode) {
        AddressEntity address = addressRepository.findByZipcode(zipcode)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        addressRepository.delete(address);
        return addressMapper.toResponse(address);
    }

    private void validateAddressDoesNotExist(String zipcode) {
        if (addressRepository.findByZipcode(zipcode).isPresent()) {
            throw new AddressCreatedException("Zipcode " + zipcode + " already exists");
        }
    }
}