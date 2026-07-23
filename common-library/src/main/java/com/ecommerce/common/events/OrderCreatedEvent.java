package com.ecommerce.common.events;

import com.ecommerce.common.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private Long orderId;

    private String orderNumber;

    private BigDecimal totalAmount;

    private PaymentMethod paymentMethod;

    private LocalDateTime eventTime;

}