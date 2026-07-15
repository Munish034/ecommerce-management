package com.ecommerce.orderservice.util;



import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class OrderNumberGenerator {

    private final AtomicLong counter = new AtomicLong(1);

    public String generateOrderNumber() {

        return "ORD"
                + Year.now().getValue()
                + String.format("%06d", counter.getAndIncrement());

    }

}