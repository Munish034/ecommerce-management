package com.ecommerce.inventoryservice.dto.request;

import com.ecommerce.inventoryservice.enums.ProductCategory;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    @NotBlank(message = "Product Code is required")
    private String productCode;

    @NotBlank(message = "Product Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Available Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer availableQuantity;

    @NotNull(message = "Category is required")
    private ProductCategory category;

}