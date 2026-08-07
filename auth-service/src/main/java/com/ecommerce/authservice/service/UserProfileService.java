package com.ecommerce.authservice.service;

import com.ecommerce.authservice.dto.request.UpdateProfileRequest;
import com.ecommerce.authservice.dto.response.ProfileResponse;

public interface UserProfileService {

    ProfileResponse getMyProfile();

    ProfileResponse updateMyProfile(UpdateProfileRequest request);

    ProfileResponse getProfileByUserId(Long userId);

}