package com.ecommerce.paymentservice.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransactionIdGenerator {

    public String generateTransactionId() {

        return "TXN-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0,16)
                .toUpperCase();

    }

}