package com.ecommerce.paymentservice.dto.response;

import com.ecommerce.common.enums.PaymentMethod;
import com.ecommerce.paymentservice.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;

    private String paymentId;

    private String orderNumber;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String transactionId;

    private LocalDateTime createdAt;

}