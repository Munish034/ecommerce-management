package com.ecommerce.authservice.dto.response;

import lombok.*;

@Getter
@Builder
public class LoginResponse {

    private String token;

    private String tokenType;

    private Long userId;
    private String refreshToken;
    private String username;

    private String email;

}
