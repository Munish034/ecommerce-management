package com.ecommerce.common.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailsDto {

    private Long id;

    private String productCode;

    private String name;

    private BigDecimal price;

    private Integer availableQuantity;

}