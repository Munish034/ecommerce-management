package com.ecommerce.authservice.service.impl;

import com.ecommerce.authservice.dto.request.UpdateProfileRequest;
import com.ecommerce.authservice.dto.response.ProfileResponse;
import com.ecommerce.authservice.entity.User;
import com.ecommerce.authservice.entity.UserProfile;
import com.ecommerce.authservice.mapper.UserProfileMapper;
import com.ecommerce.authservice.repository.UserProfileRepository;
import com.ecommerce.authservice.repository.UserRepository;
import com.ecommerce.authservice.service.UserProfileService;
import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final UserProfileMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {

        Long userId = SecurityUtils.getCurrentUserId();

        UserProfile profile = getOrCreateProfile(userId);

        return mapper.toResponse(profile);
    }

    @Override
    public ProfileResponse updateMyProfile(UpdateProfileRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();

        UserProfile profile = getOrCreateProfile(userId);

        mapper.updateProfile(request, profile);

        UserProfile updated = profileRepository.save(profile);

        return mapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUserId(Long userId) {

        UserProfile profile = getOrCreateProfile(userId);

        return mapper.toResponse(profile);
    }

    /**
     * Creates profile automatically if it doesn't exist.
     */
    private UserProfile getOrCreateProfile(Long userId) {

        return profileRepository.findByUserId(userId)
                .orElseGet(() -> createProfile(userId));
    }

    private UserProfile createProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found",
                                ErrorCode.USER_NOT_FOUND));

        UserProfile profile = UserProfile.builder()
                .user(user)
                .build();

        return profileRepository.save(profile);
    }
}