package com.ecommerce.orderservice.service;




import com.ecommerce.orderservice.entity.Order;

public interface PricingService {

    void calculatePrice(Order order);

}