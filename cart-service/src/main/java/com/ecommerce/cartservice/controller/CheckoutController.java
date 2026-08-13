package com.ecommerce.cartservice.controller;

import com.ecommerce.cartservice.dto.request.CheckoutRequest;
import com.ecommerce.cartservice.dto.response.CartResponse;
import com.ecommerce.cartservice.service.CheckoutService;
import com.ecommerce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> checkout(
            @Valid @RequestBody CheckoutRequest request) {

        CartResponse response =
                checkoutService.checkout(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Checkout completed successfully.",
                        response
                )
        );
    }
}