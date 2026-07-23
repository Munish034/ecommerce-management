package com.ecommerce.orderservice.kafka;

import com.ecommerce.common.events.OrderCancelledEvent;
import com.ecommerce.common.events.OrderCreatedEvent;
import com.ecommerce.common.constant.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.ORDER_CREATED,
                event);
    }

    public void publishOrderCancelled(OrderCancelledEvent event) {

        kafkaTemplate.send(
                KafkaTopics.ORDER_CANCELLED,
                event);
    }

}