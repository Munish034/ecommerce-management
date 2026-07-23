package com.ecommerce.orderservice.service;



import com.ecommerce.orderservice.dto.request.CreateOrderRequest;
import com.ecommerce.orderservice.dto.request.OrderSearchRequest;
import com.ecommerce.orderservice.dto.response.OrderResponse;
import org.springframework.data.domain.Page;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse cancelOrder(Long orderId);
    void deleteOrder(Long orderId);
    OrderResponse getOrderById(Long orderId);
    Page<OrderResponse> searchOrders(
            OrderSearchRequest request,
            int page,
            int size,
            String sortBy,
            String direction);
}