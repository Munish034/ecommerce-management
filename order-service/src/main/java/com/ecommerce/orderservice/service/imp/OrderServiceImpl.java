package com.ecommerce.orderservice.service.imp;

import com.ecommerce.orderservice.dto.CreateOrderRequest;
import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.enums.OrderStatus;
import com.ecommerce.orderservice.enums.PaymentStatus;
import com.ecommerce.orderservice.mapper.OrderMapper;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.OrderService;
import com.ecommerce.orderservice.service.PricingService;
import com.ecommerce.orderservice.util.OrderNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;

    private final OrderMapper mapper;

    private final PricingService pricingService;

    private final OrderNumberGenerator generator;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {

        Order order = mapper.toEntity(request);

        order.setOrderNumber(generator.generateOrderNumber());

        order.setOrderStatus(OrderStatus.CREATED);

        order.setPaymentStatus(PaymentStatus.PENDING);

        /*
         *
         * NEXT SPRINT
         *
         * Inventory Service
         *
         * Product Name
         *
         * Unit Price
         *
         * Total Price
         *
         */

        pricingService.calculatePrice(order);

        Order savedOrder = repository.save(order);

        return mapper.toResponse(savedOrder);

    }

}