package com.ecommerce.orderservice.client;

import com.ecommerce.common.response.ApiResponse;

import com.ecommerce.orderservice.dto.InventoryProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "INVENTORY-SERVICE"
)
public interface InventoryClient {

    @GetMapping("/api/products/{id}")
    ApiResponse<InventoryProductResponse> getProduct(
            @PathVariable Long id);

}