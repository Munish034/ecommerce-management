package com.ecommerce.paymentservice.util;

import com.ecommerce.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentIdGenerator {

    private final PaymentRepository paymentRepository;

    public String generatePaymentId() {

        long nextNumber =
                paymentRepository.getNextPaymentSequence();

        return "PAY"
                + java.time.Year.now().getValue()
                + String.format("%06d", nextNumber);
    }
}