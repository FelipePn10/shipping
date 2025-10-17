package redirex.shipping.mapper;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import redirex.shipping.dto.request.AddressRequest;
import redirex.shipping.dto.request.CreateAddressRequest;
import redirex.shipping.dto.response.AddressResponse;
import redirex.shipping.entity.AddressEntity;
import redirex.shipping.entity.UserEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Arch Linux)"
)
@Component
public class AddressMapperImpl implements AddressMapper {

    @Override
    public AddressResponse toResponse(AddressEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UUID userId = null;
        UUID id = null;
        String recipientName = null;
        String street = null;
        String complement = null;
        String city = null;
        String state = null;
        String zipcode = null;
        String country = null;
        String phone = null;
        AddressEntity.ResidenceType residenceType = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        userId = entityUserId( entity );
        id = entity.getId();
        recipientName = entity.getRecipientName();
        street = entity.getStreet();
        complement = entity.getComplement();
        city = entity.getCity();
        state = entity.getState();
        zipcode = entity.getZipcode();
        country = entity.getCountry();
        phone = entity.getPhone();
        residenceType = entity.getResidenceType();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();

        AddressResponse addressResponse = new AddressResponse( id, userId, recipientName, street, complement, city, state, zipcode, country, phone, residenceType, createdAt, updatedAt );

        return addressResponse;
    }

    @Override
    public AddressEntity toEntity(AddressRequest dto) {
        if ( dto == null ) {
            return null;
        }

        AddressEntity.AddressEntityBuilder addressEntity = AddressEntity.builder();

        addressEntity.recipientName( dto.recipientName() );
        addressEntity.street( dto.street() );
        addressEntity.complement( dto.complement() );
        addressEntity.city( dto.city() );
        addressEntity.state( dto.state() );
        addressEntity.zipcode( dto.zipcode() );
        addressEntity.country( dto.country() );
        addressEntity.phone( dto.phone() );
        addressEntity.residenceType( dto.residenceType() );

        return addressEntity.build();
    }

    @Override
    public AddressEntity toEntity(CreateAddressRequest request) {
        if ( request == null ) {
            return null;
        }

        AddressEntity.AddressEntityBuilder addressEntity = AddressEntity.builder();

        addressEntity.recipientName( request.recipientName() );
        addressEntity.street( request.street() );
        addressEntity.complement( request.complement() );
        addressEntity.city( request.city() );
        addressEntity.state( request.state() );
        addressEntity.zipcode( request.zipcode() );
        addressEntity.country( request.country() );
        addressEntity.phone( request.phone() );
        addressEntity.residenceType( request.residenceType() );

        return addressEntity.build();
    }

    @Override
    public void updateEntityFromDto(AddressRequest dto, AddressEntity entity) {
        if ( dto == null ) {
            return;
        }

        entity.setRecipientName( dto.recipientName() );
        entity.setStreet( dto.street() );
        entity.setComplement( dto.complement() );
        entity.setCity( dto.city() );
        entity.setState( dto.state() );
        entity.setZipcode( dto.zipcode() );
        entity.setCountry( dto.country() );
        entity.setPhone( dto.phone() );
        entity.setResidenceType( dto.residenceType() );
    }

    @Override
    public AddressRequest toDTO(CreateAddressRequest request) {
        if ( request == null ) {
            return null;
        }

        String recipientName = null;
        String street = null;
        String complement = null;
        String city = null;
        String state = null;
        String zipcode = null;
        String country = null;
        String phone = null;
        UUID userId = null;
        AddressEntity.ResidenceType residenceType = null;

        recipientName = request.recipientName();
        street = request.street();
        complement = request.complement();
        city = request.city();
        state = request.state();
        zipcode = request.zipcode();
        country = request.country();
        phone = request.phone();
        userId = request.userId();
        residenceType = request.residenceType();

        AddressRequest addressRequest = new AddressRequest( recipientName, street, complement, city, state, zipcode, country, phone, userId, residenceType );

        return addressRequest;
    }

    private UUID entityUserId(AddressEntity addressEntity) {
        UserEntity user = addressEntity.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }
}
