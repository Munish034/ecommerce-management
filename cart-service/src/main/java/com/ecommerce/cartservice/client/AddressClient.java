package com.ecommerce.cartservice.client;

import com.ecommerce.cartservice.dto.response.AddressResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AddressClient {

    @GetMapping("/api/v1/addresses/{addressId}")
    AddressResponse getAddress(
            @PathVariable("addressId") Long addressId
    );
}