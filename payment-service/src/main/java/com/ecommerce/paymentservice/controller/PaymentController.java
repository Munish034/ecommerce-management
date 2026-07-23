package com.ecommerce.paymentservice.controller;

import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.paymentservice.dto.request.CreatePaymentRequest;
import com.ecommerce.paymentservice.dto.response.PaymentResponse;
import com.ecommerce.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse response =
                paymentService.processPayment(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Payment Processed Successfully",
                        response));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable String paymentId) {

        PaymentResponse response =
                paymentService.getPayment(paymentId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Payment Retrieved Successfully",
                        response));
    }

}