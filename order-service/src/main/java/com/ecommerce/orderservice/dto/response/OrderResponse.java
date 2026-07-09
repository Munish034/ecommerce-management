package com.ecommerce.orderservice.dto.response;

import com.ecommerce.orderservice.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;

    private String orderNumber;

    private OrderStatus status;

    private BigDecimal totalAmount;
}
