package com.ecommerce.paymentservice.repository;

import com.ecommerce.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentId(String paymentId);

    Optional<Payment> findByOrderNumber(String orderNumber);

    Optional<Payment> findByTransactionId(String transactionId);

    boolean existsByOrderNumber(String orderNumber);

}