package com.ecommerce.paymentservice.service.impl;

import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.events.PaymentFailedEvent;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.paymentservice.dto.request.CreatePaymentRequest;
import com.ecommerce.paymentservice.dto.response.PaymentResponse;
import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.enums.PaymentStatus;
import com.ecommerce.paymentservice.kafka.PaymentEventProducer;
import com.ecommerce.paymentservice.mapper.PaymentMapper;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import com.ecommerce.paymentservice.service.PaymentService;
import com.ecommerce.paymentservice.util.PaymentIdGenerator;
import com.ecommerce.paymentservice.util.TransactionIdGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    private final PaymentMapper mapper;

    private final PaymentIdGenerator paymentIdGenerator;

    private final TransactionIdGenerator transactionIdGenerator;
    private final PaymentEventProducer paymentEventProducer;

    @Override
    public PaymentResponse processPayment(CreatePaymentRequest request) {

        if (request.getAmount().compareTo(BigDecimal.valueOf(100000)) > 0) {

            PaymentFailedEvent event = PaymentFailedEvent.builder()
                    .orderNumber(request.getOrderNumber())

                    .reason("LIMIT_EXCEEDED")
                    .build();

            paymentEventProducer.publishPaymentFailedEvent(event);

            throw new BusinessException(
                    "Payment failed. Amount exceeds limit.",
                    ErrorCode.PAYMENT_FAILED
            );
        }

        Payment payment = mapper.toEntity(request);

        payment.setPaymentId(
                paymentIdGenerator.generatePaymentId());

        payment.setTransactionId(
                transactionIdGenerator.generateTransactionId());

        /*
         * Temporary Logic
         *
         * Later this will call Razorpay/Stripe
         */

        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        Payment savedPayment =
                repository.save(payment);

        return mapper.toResponse(savedPayment);

    }

    @Override
    public PaymentResponse getPayment(String paymentId) {

        Payment payment = repository.findByPaymentId(paymentId)

                .orElseThrow(() ->

                        new ResourceNotFoundException(



                                "Payment not found : "
                                        + paymentId,  ErrorCode.PAYMENT_NOT_FOUND));

        return mapper.toResponse(payment);

    }

}