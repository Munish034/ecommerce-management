package com.ecommerce.inventoryservice.dto.response;

import com.ecommerce.inventoryservice.enums.ProductCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;

    private String productCode;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer availableQuantity;

    private ProductCategory category;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}