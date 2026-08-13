package com.ecommerce.paymentservice.repository;

import com.ecommerce.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentId(String paymentId);

    @Query("""
           SELECT COALESCE(
               MAX(CAST(SUBSTRING(p.paymentId, 8) AS long)),
               0
           ) + 1
           FROM Payment p
           WHERE p.paymentId LIKE CONCAT('PAY', FUNCTION('YEAR', CURRENT_DATE), '%')
           """)
    long getNextPaymentSequence();

    Optional<Payment> findByOrderNumber(String orderNumber);

    Optional<Payment> findByTransactionId(String transactionId);

    boolean existsByOrderNumber(String orderNumber);

}