package com.ecommerce.common.events;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent {

    private String orderNumber;

    private Long productId;

    private Integer quantity;

    private String reason;
    private LocalDateTime eventTime;
}