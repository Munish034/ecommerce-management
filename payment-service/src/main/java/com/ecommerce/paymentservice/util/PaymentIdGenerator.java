package com.ecommerce.paymentservice.util;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class PaymentIdGenerator {

    private final AtomicLong counter = new AtomicLong(1);

    public String generatePaymentId() {

        return "PAY"
                + Year.now().getValue()
                + String.format("%06d", counter.getAndIncrement());

    }

}