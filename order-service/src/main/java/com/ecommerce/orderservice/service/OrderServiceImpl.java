package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.OrderItemRequest;
import com.ecommerce.orderservice.dto.request.OrderRequest;
import com.ecommerce.orderservice.dto.response.OrderResponse;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.OrderItem;
import com.ecommerce.orderservice.enums.OrderStatus;
import com.ecommerce.orderservice.generator.OrderNumberGenerator;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.imp.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {



    private final OrderRepository orderRepository;
    private final OrderNumberGenerator orderNumberGenerator;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderNumberGenerator orderNumberGenerator) {
        this.orderRepository = orderRepository;
        this.orderNumberGenerator = orderNumberGenerator;

    }

    @Override
    public OrderResponse createOrder(OrderRequest request) {

        Order order = Order.builder()
                .orderNumber(orderNumberGenerator.generate())
                .customerId(request.getCustomerId())
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {

            BigDecimal price = BigDecimal.valueOf(1000);

            OrderItem item = OrderItem.builder()
                    .productId(itemRequest.getProductId())
                    .quantity(itemRequest.getQuantity())
                    .price(price)
                    .order(order)
                    .build();

            orderItems.add(item);

            totalAmount = totalAmount.add(
                    price.multiply(
                            BigDecimal.valueOf(
                                    itemRequest.getQuantity()
                            )
                    )
            );
        }

        order.setItems(orderItems);

        order.setTotalAmount(totalAmount);

        Order savedOrder =
                orderRepository.save(order);

        return OrderResponse.builder()
                .orderId(savedOrder.getId())
                .orderNumber(savedOrder.getOrderNumber())
                .status(savedOrder.getStatus())
                .totalAmount(savedOrder.getTotalAmount())
                .build();

    }


    @Override
    public OrderResponse getOrder(Long id) {
        return null;
    }

    @Override
    public void cancelOrder(Long id) {

    }
}