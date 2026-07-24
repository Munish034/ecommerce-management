package com.ecommerce.inventoryservice.kafka;

import com.ecommerce.common.events.OrderCancelledEvent;
import com.ecommerce.inventoryservice.dto.request.ReleaseStockRequest;
import com.ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCancelledConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "order-cancelled-topic",
            groupId = "inventory-group"
    )
    public void consume(OrderCancelledEvent event) {

        ReleaseStockRequest request = new ReleaseStockRequest();

        request.setProductId(event.getProductId());
        request.setQuantity(event.getQuantity());

        inventoryService.releaseStock(request);

        log.info("Released stock for Order {}", event.getOrderNumber());

    }
}