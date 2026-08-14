package com.ecommerce.cartservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CheckoutResponse {

    private Long orderId;

    private String orderNumber;

    private String orderStatus;

    private String paymentStatus;

    private BigDecimal finalAmount;

    private String message;
}