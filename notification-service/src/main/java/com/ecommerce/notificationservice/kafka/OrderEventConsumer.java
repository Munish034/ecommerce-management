package com.ecommerce.notificationservice.kafka;

import com.ecommerce.common.constant.KafkaTopics;
import com.ecommerce.common.events.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventConsumer {

    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "notification-group"
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Processing Order {}", event.getOrderNumber());

        throw new RuntimeException("Email Server Down");

      /*  log.info("Order Created Event Received : {}", event);

        log.info("========================================");
        log.info("ORDER Created EVENT RECEIVED");
        log.info("Order Id      : {}", event.getOrderId());
        log.info("Order Number  : {}", event.getOrderNumber());

        log.info("Event Time    : {}", event.getEventTime());
        log.info("========================================");*/

    }

}