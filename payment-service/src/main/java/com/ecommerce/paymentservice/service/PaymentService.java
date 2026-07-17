package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.request.CreatePaymentRequest;
import com.ecommerce.paymentservice.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse processPayment(CreatePaymentRequest request);

    PaymentResponse getPayment(String paymentId);

}
