package com.ecommerce.cartservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CartResponse {

    private Long cartId;

    private Long customerId;

    private List<CartItemResponse> items;

    private BigDecimal totalAmount;
}