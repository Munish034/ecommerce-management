package com.ecommerce.orderservice.mapper;



import com.ecommerce.orderservice.dto.request.CreateOrderRequest;
import com.ecommerce.orderservice.dto.request.OrderItemRequest;
import com.ecommerce.orderservice.dto.response.OrderItemResponse;
import com.ecommerce.orderservice.dto.response.OrderResponse;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    /**
     * Convert Request DTO -> Entity
     */
    public Order toEntity(CreateOrderRequest request) {

        Order order = new Order();

        order.setCustomerId(request.getCustomerId());
        order.setPaymentMethod(request.getPaymentMethod());

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {

            OrderItem item = new OrderItem();

            item.setProductId(itemRequest.getProductId());
            item.setQuantity(itemRequest.getQuantity());

            // Maintain bi-directional relationship
            item.setOrder(order);

            orderItems.add(item);
        }

        order.setOrderItems(orderItems);

        return order;
    }

    /**
     * Convert Entity -> Response DTO
     */
    public OrderResponse toResponse(Order order) {

        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (OrderItem item : order.getOrderItems()) {

            OrderItemResponse response = OrderItemResponse.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .totalPrice(item.getTotalPrice())
                    .build();

            itemResponses.add(response);
        }

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .finalAmount(order.getFinalAmount())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }

}