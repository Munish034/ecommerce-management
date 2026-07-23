package com.ecommerce.notificationservice.kafka;

import com.ecommerce.common.constant.KafkaTopics;
import com.ecommerce.common.events.OrderCancelledEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCancelledConsumer {

    @KafkaListener(
            topics = KafkaTopics.ORDER_CANCELLED,
            groupId = "notification-group"
    )
    public void consume(OrderCancelledEvent event) {

        log.info("========================================");
        log.info("ORDER CANCELLED EVENT RECEIVED");
        log.info("Order Id      : {}", event.getOrderId());
        log.info("Order Number  : {}", event.getOrderNumber());
        log.info("Reason        : {}", event.getReason());
        log.info("Event Time    : {}", event.getEventTime());
        log.info("========================================");
    }
}