package com.ecommerce.authservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ProfileResponse {

    private Long userId;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String gender;

    private LocalDate dateOfBirth;

    private String bio;

    private String profileImage;
}