package com.ecommerce.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusChangedEvent {

    private Long orderId;

    private String orderNumber;

    private String previousStatus;

    private String newStatus;

    private LocalDateTime eventTime;
}