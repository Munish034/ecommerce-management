package com.ecommerce.orderservice.kafka;

import com.ecommerce.common.events.PaymentFailedEvent;
import com.ecommerce.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFailedConsumer {
    private static final String PAYMENT_FAILED_TOPIC = "payment-failed-topic";
    private final OrderService orderService;

    @KafkaListener(
            topics = "payment-failed-topic",
            groupId = "order-group"
    )
    public void consume(PaymentFailedEvent event) {

        log.info("Received PaymentFailedEvent for Order : {}",
                event.getOrderNumber());

        orderService.cancelOrderByOrderNumber(event.getOrderNumber());

    }
}