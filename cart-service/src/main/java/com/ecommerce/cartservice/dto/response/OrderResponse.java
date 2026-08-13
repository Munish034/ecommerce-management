package com.ecommerce.cartservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {

    private Long id;

    private String orderNumber;

    private String orderStatus;

    private String paymentStatus;

    private java.math.BigDecimal finalAmount;
}