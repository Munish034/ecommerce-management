package com.ecommerce.orderservice.dto;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {

    @NotNull(message = "Product Id is required")
    private Long productId;

    @Min(value = 1, message = "Quantity should be at least 1")
    private Integer quantity;

}