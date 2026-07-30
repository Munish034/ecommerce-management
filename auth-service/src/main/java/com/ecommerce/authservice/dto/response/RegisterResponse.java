package com.ecommerce.authservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String message;
}