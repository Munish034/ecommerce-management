package com.ecommerce.inventoryservice.mapper;



import com.ecommerce.inventoryservice.dto.request.CreateProductRequest;
import com.ecommerce.inventoryservice.dto.response.ProductResponse;
import com.ecommerce.inventoryservice.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    /**
     * Request DTO -> Entity
     */
    public Product toEntity(CreateProductRequest request) {

        return Product.builder()
                .productCode(request.getProductCode())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .availableQuantity(request.getAvailableQuantity())
                .category(request.getCategory())
                .build();

    }

    /**
     * Entity -> Response DTO
     */
    public ProductResponse toResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .availableQuantity(product.getAvailableQuantity())
                .category(product.getCategory())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();

    }

}