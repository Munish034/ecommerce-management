package com.ecommerce.paymentservice.mapper;

import com.ecommerce.paymentservice.dto.request.CreatePaymentRequest;
import com.ecommerce.paymentservice.dto.response.PaymentResponse;
import com.ecommerce.paymentservice.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(CreatePaymentRequest request) {
        System.out.println("payment "+request.getPaymentMethod());
        return Payment.builder()
                .orderNumber(request.getOrderNumber())
                .amount(request.getAmount())
                .customerId(request.getCustomerId())
                .paymentMethod(request.getPaymentMethod())
                .build();
    }

    public PaymentResponse toResponse(Payment payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentId(payment.getPaymentId())
                .orderNumber(payment.getOrderNumber())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }

}