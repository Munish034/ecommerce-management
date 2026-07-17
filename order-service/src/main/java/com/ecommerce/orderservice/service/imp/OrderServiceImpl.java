package com.ecommerce.orderservice.service.imp;

import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.orderservice.client.PaymentClient;
import com.ecommerce.orderservice.dto.client.PaymentRequest;
import com.ecommerce.orderservice.dto.client.PaymentResponse;
import com.ecommerce.orderservice.dto.client.ReserveStockRequest;
import com.ecommerce.orderservice.dto.request.CreateOrderRequest;
import com.ecommerce.orderservice.dto.response.InventoryProductResponse;
import com.ecommerce.orderservice.dto.response.OrderResponse;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.OrderItem;
import com.ecommerce.orderservice.enums.OrderStatus;
import com.ecommerce.orderservice.enums.PaymentStatus;
import com.ecommerce.orderservice.mapper.OrderMapper;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.OrderService;
import com.ecommerce.orderservice.service.PricingService;
import com.ecommerce.orderservice.util.OrderNumberGenerator;
import com.ecommerce.orderservice.client.InventoryClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;

    private final OrderMapper mapper;

    private final PricingService pricingService;
    private final PaymentClient paymentClient;

    private final OrderNumberGenerator generator;
    private final InventoryClient inventoryClient;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        // Convert Request DTO to Entity
        Order order = mapper.toEntity(request);

        order.setOrderNumber(generator.generateOrderNumber());

        order.setOrderStatus(OrderStatus.CREATED);

        order.setPaymentStatus(PaymentStatus.PENDING);

        // Fetch Product Details from Inventory Service
        for (OrderItem item : order.getOrderItems()) {

            ApiResponse<InventoryProductResponse> apiResponse =
                    inventoryClient.getProduct(item.getProductId());

            InventoryProductResponse product = apiResponse.getData();

            if (product == null) {

                throw new ResourceNotFoundException(

                        "Product not found: " + item.getProductId(),ErrorCode.PRODUCT_NOT_FOUND
                );
            }

            inventoryClient.reserveStock(
                    ReserveStockRequest.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .build());

            item.setProductName(product.getName());

            item.setUnitPrice(product.getPrice());

            item.setTotalPrice(
                    product.getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()))
            );

        }

        pricingService.calculatePrice(order);
        ApiResponse<PaymentResponse> paymentResponse =
                paymentClient.processPayment(

                        PaymentRequest.builder()

                                .orderNumber(order.getOrderNumber())

                                .amount(order.getFinalAmount())

                                .paymentMethod(order.getPaymentMethod())

                                .build()

                );


        Order savedOrder = repository.save(order);

        return mapper.toResponse(savedOrder);

    }
}