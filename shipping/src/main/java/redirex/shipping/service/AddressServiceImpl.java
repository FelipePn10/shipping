package redirex.shipping.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import redirex.shipping.dto.request.AddressRequest;
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
    @PreAuthorize("@permissionService.isOwner(#dto.userId)")
    public AddressResponse createdAddress(@Valid AddressRequest dto) {
        logger.info("Creating address for zipcode: {}", dto.getZipcode());
        validateAddressDoesNotExist(dto.getZipcode());

        UserEntity user = getAuthenticatedUser();
        AddressEntity address = addressMapper.toEntity(dto);
        address.setUser(user);
        address.setCreatedAt(LocalDateTime.now());

        AddressEntity saved = addressRepository.save(address);
        logger.info("Address created successfully: {}", saved.getId());
        return addressMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("@permissionService.isOwnerOrAdmin(#dto.userId)")
    public AddressResponse updateAddress(String zipcode, @Valid AddressRequest dto) {
        logger.info("Updating address for zipcode: {}", zipcode);
        AddressEntity address = addressRepository.findByZipcode(zipcode)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));

        UserEntity user = getAuthenticatedUser();
        if (!address.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Usuário não tem permissão para alterar este endereço");
        }

        if (!dto.getZipcode().equals(zipcode)) {
            validateAddressDoesNotExist(dto.getZipcode());
        }

        addressMapper.updateEntityFromDto(dto, address);
        address.setUpdatedAt(LocalDateTime.now());

        AddressEntity updated = addressRepository.save(address);
        logger.info("Address updated successfully: {}", updated.getId());
        return addressMapper.toResponse(updated);
    }

    @Override
    @Transactional
    @PreAuthorize("@permissionService.isOwnerOrAdminByZipcode(#zipcode)")
    public void deleteAddress(String zipcode) {
        AddressEntity address = addressRepository.findByZipcode(zipcode)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));

        addressRepository.delete(address);
        logger.info("Address deleted successfully: {}", zipcode);
    }


    private UserEntity getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private void validateAddressDoesNotExist(String zipcode) {
        if (addressRepository.findByZipcode(zipcode).isPresent()) {
            throw new AddressCreatedException("CEP " + zipcode + " já existe");
        }
    }
}