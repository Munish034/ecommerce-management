package com.ecommerce.orderservice.dto.client;

import com.ecommerce.common.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    private String orderNumber;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

}