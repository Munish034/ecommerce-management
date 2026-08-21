package com.ecommerce.orderservice.dto.request;

import com.ecommerce.orderservice.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;
}