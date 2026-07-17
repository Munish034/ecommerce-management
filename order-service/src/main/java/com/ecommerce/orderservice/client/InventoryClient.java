package com.ecommerce.orderservice.client;

import com.ecommerce.common.response.ApiResponse;

import com.ecommerce.orderservice.dto.client.ReserveStockRequest;
import com.ecommerce.orderservice.dto.response.InventoryProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "INVENTORY-SERVICE"
)
public interface InventoryClient {

    @GetMapping("/api/products/{id}")
    ApiResponse<InventoryProductResponse> getProduct(
            @PathVariable Long id);
    @PostMapping("/api/products/reserve")
    ApiResponse<InventoryProductResponse> reserveStock(
            @RequestBody ReserveStockRequest request);

}