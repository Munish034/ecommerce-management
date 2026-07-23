package com.ecommerce.orderservice.client;

import com.ecommerce.common.response.ApiResponse;

import com.ecommerce.orderservice.dto.client.ReleaseStockRequest;
import com.ecommerce.orderservice.dto.client.ReserveStockRequest;
import com.ecommerce.orderservice.dto.response.InventoryProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {

    @GetMapping("/api/v1/products/{id}")
    ApiResponse<InventoryProductResponse> getProduct(
            @PathVariable Long id);

    @PostMapping("/api/v1/products/reserve")
    ApiResponse<InventoryProductResponse> reserveStock(
            @RequestBody ReserveStockRequest request);

    @PostMapping("/api/v1/products/release")
    ApiResponse<InventoryProductResponse> releaseStock(
            @RequestBody ReleaseStockRequest request);
}