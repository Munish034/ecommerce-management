package com.ecommerce.orderservice.util;

import com.ecommerce.orderservice.entity.OrderNumberSequence;
import com.ecommerce.orderservice.repository.OrderNumberSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class OrderNumberGenerator {

    private final OrderNumberSequenceRepository sequenceRepository;

    @Transactional
    public String generateOrderNumber() {

        OrderNumberSequence sequence =
                sequenceRepository.findSequenceForUpdate()
                        .orElseGet(() ->
                                sequenceRepository.save(
                                        OrderNumberSequence.builder()
                                                .id(1)
                                                .nextNumber(1L)
                                                .build()
                                )
                        );

        long currentNumber = sequence.getNextNumber();

        sequence.setNextNumber(currentNumber + 1);

        sequenceRepository.save(sequence);

        return "ORD"
                + Year.now().getValue()
                + String.format("%06d", currentNumber);
    }
}