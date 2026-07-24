package com.ecommerce.orderservice.service;

public interface OutboxService {

    void saveEvent(
            String aggregateType,
            Long aggregateId,
            String eventType,
            Object event
    );

}