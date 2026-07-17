package com.ecommerce.orderservice.client;

import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.orderservice.dto.client.PaymentRequest;
import com.ecommerce.orderservice.dto.client.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {

    @PostMapping("/api/payments")
    ApiResponse<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request);

}