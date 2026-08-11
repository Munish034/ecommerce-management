package com.ecommerce.authservice.mapper;

import com.ecommerce.authservice.dto.request.CreateAddressRequest;
import com.ecommerce.authservice.dto.request.UpdateAddressRequest;
import com.ecommerce.authservice.dto.response.AddressResponse;
import com.ecommerce.authservice.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Address toEntity(CreateAddressRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            UpdateAddressRequest request,
            @MappingTarget Address address
    );

    AddressResponse toResponse(Address address);
}