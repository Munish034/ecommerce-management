package com.ecommerce.orderservice.service.imp;

import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.enums.OrderStatus;
import com.ecommerce.orderservice.enums.PaymentStatus;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.OrderTransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderTransactionServiceImpl
        implements OrderTransactionService {

    private final OrderRepository repository;


    @Override
    @Transactional
    public Order savePendingOrder(Order order) {

        return repository.save(order);
    }

    @Override
    @Transactional
    public Order confirmOrder(Order order) {

        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus(PaymentStatus.PAID);

        return repository.save(order);
    }

    @Override
    @Transactional
    public Order cancelOrder(Order order) {

        order.setOrderStatus(OrderStatus.CANCELLED);

        return repository.save(order);
    }
}