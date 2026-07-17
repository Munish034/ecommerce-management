package com.ecommerce.orderservice.dto.client;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveStockRequest {

    private Long productId;

    private Integer quantity;

}