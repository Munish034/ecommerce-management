package com.ecommerce.orderservice.dto.client;

import com.ecommerce.orderservice.enums.PaymentStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private String paymentId;

    private String transactionId;

    private PaymentStatus paymentStatus;

}