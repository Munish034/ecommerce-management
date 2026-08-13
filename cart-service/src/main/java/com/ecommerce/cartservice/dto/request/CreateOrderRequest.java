package com.ecommerce.cartservice.dto.request;

import com.ecommerce.common.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    private Long customerId;

    private PaymentMethod paymentMethod;

    private List<OrderItemRequest> items;
}