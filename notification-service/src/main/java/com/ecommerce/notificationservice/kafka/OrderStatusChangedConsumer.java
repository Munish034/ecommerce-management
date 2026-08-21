package com.ecommerce.notificationservice.kafka;

import com.ecommerce.common.constant.KafkaTopics;
import com.ecommerce.common.events.OrderStatusChangedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusChangedConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = KafkaTopics.ORDER_STATUS_CHANGED,
            groupId = "notification-service-group"
    )
    public void consume(String payload) {

        try {

            OrderStatusChangedEvent event =
                    objectMapper.readValue(
                            payload,
                            OrderStatusChangedEvent.class
                    );

            log.info(
                    "Received OrderStatusChangedEvent. Order [{}], {} -> {}",
                    event.getOrderNumber(),
                    event.getPreviousStatus(),
                    event.getNewStatus()
            );

            log.info(
                    "Order [{}] status changed to [{}]",
                    event.getOrderNumber(),
                    event.getNewStatus()
            );

        } catch (Exception exception) {

            log.error(
                    "Failed to process OrderStatusChangedEvent: {}",
                    payload,
                    exception
            );
        }
    }
}