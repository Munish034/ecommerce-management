package com.ecommerce.inventoryservice.service;



import com.ecommerce.inventoryservice.dto.request.CreateProductRequest;
import com.ecommerce.inventoryservice.dto.request.ReserveStockRequest;
import com.ecommerce.inventoryservice.dto.response.ProductResponse;

import java.util.List;

public interface InventoryService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse getProduct(Long id);

    List<ProductResponse> getAllProducts();

    ProductResponse reserveStock(ReserveStockRequest request);

}