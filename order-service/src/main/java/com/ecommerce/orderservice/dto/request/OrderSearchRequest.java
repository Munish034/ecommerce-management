package com.ecommerce.orderservice.dto.request;

import com.ecommerce.orderservice.enums.OrderStatus;
import com.ecommerce.orderservice.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderSearchRequest {

    private String orderNumber;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;
}