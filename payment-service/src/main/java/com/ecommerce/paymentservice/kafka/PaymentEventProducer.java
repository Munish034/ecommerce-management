package com.ecommerce.paymentservice.kafka;

import com.ecommerce.common.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    private static final String PAYMENT_FAILED_TOPIC = "payment-failed-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentFailedEvent(PaymentFailedEvent event) {

        log.info("Publishing PaymentFailedEvent for Order [{}]", event.getOrderNumber());

        kafkaTemplate.send(PAYMENT_FAILED_TOPIC, event.getOrderNumber(), event);
    }
}