package com.ecommerce.authservice.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateProfileRequest {

    @Size(max = 15)
    @Pattern(
            regexp = "^[0-9]{10,15}$",
            message = "Invalid phone number"
    )
    private String phone;

    @Size(max = 10)
    private String gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 500)
    private String bio;

    @Size(max = 500)
    private String profileImage;
}