package com.ecommerce.orderservice.generator;

import org.springframework.stereotype.Component;

@Component
public class OrderNumberGenerator {

    public String generate() {

        return "ORD-"
                + System.currentTimeMillis();

    }
}