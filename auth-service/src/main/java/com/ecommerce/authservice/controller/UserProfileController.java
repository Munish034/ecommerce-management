package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.dto.request.UpdateProfileRequest;
import com.ecommerce.authservice.dto.response.ProfileResponse;
import com.ecommerce.authservice.service.UserProfileService;
import com.ecommerce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService profileService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Profile fetched successfully.",
                        profileService.getMyProfile()
                )
        );
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Profile updated successfully.",
                        profileService.updateMyProfile(request)
                )
        );
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Profile fetched successfully.",
                        profileService.getProfileByUserId(userId)
                )
        );
    }
}