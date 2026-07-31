package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.dto.request.LoginRequest;
import com.ecommerce.authservice.dto.request.RefreshTokenRequest;
import com.ecommerce.authservice.dto.request.RegisterRequest;
import com.ecommerce.authservice.dto.response.LoginResponse;
import com.ecommerce.authservice.dto.response.RegisterResponse;
import com.ecommerce.authservice.service.AuthService;
import com.ecommerce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthService authenticationService;
    @GetMapping("test")
    public String test(){

        return "test";
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "User registered successfully.",response

                ));
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        System.out.println("login");
        LoginResponse response = authService.login(request);
        System.out.println("login response: " + response);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successful.",
                        response
                )
        );
    }
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        LoginResponse response = authenticationService.refreshToken(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Access token refreshed successfully.",
                        response
                )
        );
    }
}