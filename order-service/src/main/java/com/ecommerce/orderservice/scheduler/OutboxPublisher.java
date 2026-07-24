package com.ecommerce.orderservice.scheduler;

import com.ecommerce.orderservice.entity.OutboxEvent;
import com.ecommerce.orderservice.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository repository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 50000000)
    public void publishEvents() {

        List<OutboxEvent> events = repository.findTop100ByPublishedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : events) {

            try {

                kafkaTemplate.send(
                        getTopic(event.getEventType()),
                        event.getPayload()
                ).get();

                event.setPublished(true);
                event.setPublishedAt(LocalDateTime.now());

                repository.save(event);

                log.info("Outbox Event Published : {}", event.getEventType());

            } catch (Exception ex) {

                log.error("Failed to publish Outbox Event : {}", event.getId(), ex);

            }

        }

    }

    private String getTopic(String eventType) {

        return switch (eventType) {

            case "ORDER_CREATED" -> "order-created-topic";

            case "ORDER_CANCELLED" -> "order-cancelled-topic";

            default -> throw new IllegalArgumentException(
                    "Unknown Event Type : " + eventType);
        };

    }

}