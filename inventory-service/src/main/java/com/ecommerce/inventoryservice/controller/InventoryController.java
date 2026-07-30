package com.ecommerce.inventoryservice.controller;


import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.inventoryservice.dto.request.CreateProductRequest;
import com.ecommerce.inventoryservice.dto.request.ReleaseStockRequest;
import com.ecommerce.inventoryservice.dto.request.ReserveStockRequest;
import com.ecommerce.inventoryservice.dto.response.ProductResponse;
import com.ecommerce.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse response = inventoryService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Product Created Successfully",
                        response));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable Long id) {
        System.out.println("===== Inventory Controller Hit =====");

        ProductResponse response = inventoryService.getProduct(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Product Retrieved Successfully",
                        response));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {

        List<ProductResponse> response = inventoryService.getAllProducts();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Products Retrieved Successfully",
                        response));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<ProductResponse>> reserveStock(
            @Valid @RequestBody ReserveStockRequest request) {

        ProductResponse response = inventoryService.reserveStock(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Stock Reserved Successfully",
                        response));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @PostMapping("/release")
    public ResponseEntity<ApiResponse<ProductResponse>> releaseStock(
            @Valid @RequestBody ReleaseStockRequest request) {

        ProductResponse response = inventoryService.releaseStock(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Stock Released Successfully",
                        response
                )
        );
    }

}