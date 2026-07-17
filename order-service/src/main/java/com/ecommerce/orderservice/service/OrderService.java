package com.ecommerce.orderservice.service;



import com.ecommerce.orderservice.dto.request.CreateOrderRequest;
import com.ecommerce.orderservice.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

}