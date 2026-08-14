package com.ecommerce.cartservice.service.impl;

import com.ecommerce.cartservice.client.AddressClient;
import com.ecommerce.cartservice.client.OrderClient;
import com.ecommerce.cartservice.dto.request.CheckoutRequest;
import com.ecommerce.cartservice.dto.request.CreateOrderRequest;
import com.ecommerce.cartservice.dto.request.OrderItemRequest;
import com.ecommerce.cartservice.dto.response.AddressResponse;
import com.ecommerce.cartservice.dto.response.CheckoutResponse;
import com.ecommerce.cartservice.dto.response.OrderResponse;
import com.ecommerce.cartservice.entity.Cart;
import com.ecommerce.cartservice.repository.CartRepository;
import com.ecommerce.cartservice.service.CheckoutService;
import com.ecommerce.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final OrderClient orderClient;
    private final AddressClient addressClient;

    @Override
    public CheckoutResponse checkout(
            CheckoutRequest request) {

        Long customerId =
                SecurityUtils.getCurrentUserId();

        // Validate address ownership
        AddressResponse address =
                addressClient.getAddress(
                        request.getShippingAddressId()
                );

        Cart cart =
                cartRepository.findByCustomerId(customerId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Cart not found."
                                ));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot checkout with an empty cart."
            );
        }

        CreateOrderRequest orderRequest =
                CreateOrderRequest.builder()
                        .customerId(customerId)
                        .paymentMethod(
                                request.getPaymentMethod()
                        )
                        .items(
                                cart.getItems()
                                        .stream()
                                        .map(item ->
                                                OrderItemRequest.builder()
                                                        .productId(
                                                                item.getProductId()
                                                        )
                                                        .quantity(
                                                                item.getQuantity()
                                                        )
                                                        .build()
                                        )
                                        .collect(Collectors.toList())
                        )
                        .build();

        // Existing Order Service handles
        // inventory, pricing, payment, Saga and Outbox.
        OrderResponse order =
                orderClient.createOrder(orderRequest);

        // Clear cart only after successful order creation.
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);

        cartRepository.save(cart);

        return CheckoutResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .finalAmount(order.getFinalAmount())
                .message("Checkout completed successfully.")
                .build();
    }
}