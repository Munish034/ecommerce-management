package com.ecommerce.authservice.mapper;

import com.ecommerce.authservice.dto.request.UpdateProfileRequest;
import com.ecommerce.authservice.dto.response.ProfileResponse;
import com.ecommerce.authservice.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "email", source = "user.email")
    ProfileResponse toResponse(UserProfile profile);

    void updateProfile(
            UpdateProfileRequest request,
            @MappingTarget UserProfile profile
    );
}