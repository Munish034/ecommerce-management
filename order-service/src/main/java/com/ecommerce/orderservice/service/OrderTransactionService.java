package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.entity.Order;

public interface OrderTransactionService {

    Order savePendingOrder(Order order);

    Order confirmOrder(Order order);

    Order cancelOrder(Order order);

}