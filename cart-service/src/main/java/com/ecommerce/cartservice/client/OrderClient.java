package com.ecommerce.cartservice.client;

import com.ecommerce.cartservice.dto.request.CreateOrderRequest;
import com.ecommerce.cartservice.dto.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service")
public interface OrderClient {

    @PostMapping("/api/v1/orders")
    OrderResponse createOrder(
            @RequestBody CreateOrderRequest request
    );
}