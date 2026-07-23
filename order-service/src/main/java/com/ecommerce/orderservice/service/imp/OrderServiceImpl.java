package com.ecommerce.orderservice.service.imp;

import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.events.OrderCancelledEvent;
import com.ecommerce.common.events.OrderCreatedEvent;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.orderservice.client.InventoryGateway;
import com.ecommerce.orderservice.client.PaymentClient;
import com.ecommerce.orderservice.dto.client.PaymentRequest;
import com.ecommerce.orderservice.dto.client.PaymentResponse;
import com.ecommerce.orderservice.dto.client.ReleaseStockRequest;
import com.ecommerce.orderservice.dto.client.ReserveStockRequest;
import com.ecommerce.orderservice.dto.request.CreateOrderRequest;
import com.ecommerce.orderservice.dto.request.OrderSearchRequest;
import com.ecommerce.orderservice.dto.response.InventoryProductResponse;
import com.ecommerce.orderservice.dto.response.OrderResponse;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.OrderItem;
import com.ecommerce.orderservice.enums.OrderStatus;
import com.ecommerce.common.enums.PaymentMethod;
import com.ecommerce.orderservice.kafka.OrderEventProducer;
import com.ecommerce.orderservice.mapper.OrderMapper;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.OrderService;
import com.ecommerce.orderservice.service.PricingService;
import com.ecommerce.orderservice.specification.OrderSpecification;
import com.ecommerce.orderservice.util.OrderNumberGenerator;
import com.ecommerce.orderservice.client.InventoryClient;
import com.ecommerce.orderservice.enums.PaymentStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;

    private final OrderMapper mapper;

    private final PricingService pricingService;
    private final PaymentClient paymentClient;
    private final OrderEventProducer orderEventProducer;

    private final OrderNumberGenerator generator;
    private final InventoryClient inventoryClient;
    private final InventoryGateway inventoryGateway;


    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "orderNumber",
            "orderStatus",
            "paymentStatus",
            "finalAmount"
    );

     /////create order /////

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        Order order = buildOrder(request);
        List<ReleaseStockRequest> reservedItems = new ArrayList<>();
        try {

            reserveInventory(order, reservedItems);

            pricingService.calculatePrice(order);

            processPayment(order);

            Order savedOrder = repository.save(order);
            OrderCreatedEvent event = OrderCreatedEvent.builder()
                    .orderId(savedOrder.getId())
                    .orderNumber(savedOrder.getOrderNumber())
                    .totalAmount(savedOrder.getFinalAmount())
                    .paymentMethod(savedOrder.getPaymentMethod())
                    .eventTime(LocalDateTime.now())
                    .build();
            orderEventProducer.publishOrderCreated(event);

            return mapper.toResponse(savedOrder);

        } catch (Exception ex) {

            releaseReservedStock(reservedItems);

            throw ex;
        }
    }


    private Order buildOrder(CreateOrderRequest request) {

        Order order = mapper.toEntity(request);

        order.setOrderNumber(generator.generateOrderNumber());
        order.setOrderStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);

        // IMPORTANT
        order.getOrderItems().forEach(item -> item.setOrder(order));

        return order;
    }
    private void reserveInventory(
            Order order,
            List<ReleaseStockRequest> reservedItems) {

        for (OrderItem item : order.getOrderItems()) {

            InventoryProductResponse product =
                    inventoryGateway.getProduct(item.getProductId());



            if (product == null) {

                throw new ResourceNotFoundException(
                        "Product not found: " + item.getProductId(),
                        ErrorCode.PRODUCT_NOT_FOUND);
            }

            inventoryGateway.reserveStock(
                    ReserveStockRequest.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .build());

            reservedItems.add(
                    ReleaseStockRequest.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .build());

            item.setProductName(product.getName());

            item.setUnitPrice(product.getPrice());

            item.setTotalPrice(
                    product.getPrice().multiply(
                            BigDecimal.valueOf(item.getQuantity())));
        }
    }
    private void processPayment(Order order) {

        ApiResponse<PaymentResponse> paymentResponse =
                paymentClient.processPayment(

                        PaymentRequest.builder()
                                .orderNumber(order.getOrderNumber())
                                .amount(order.getFinalAmount())
                                .paymentMethod(order.getPaymentMethod())
                                .build());

        if (paymentResponse.getData().getPaymentStatus()
                != PaymentStatus.SUCCESS) {

            throw new BusinessException(
                    "Payment Failed.",
                    ErrorCode.PAYMENT_FAILED);
        }

        order.setPaymentStatus(PaymentStatus.SUCCESS);
    }
    private void releaseReservedStock(
            List<ReleaseStockRequest> reservedItems) {

        for (ReleaseStockRequest request : reservedItems) {

            try {

                inventoryClient.releaseStock(request);

            } catch (Exception ex) {

                // We'll replace this with logging later
                System.err.println(
                        "Failed to release stock for Product Id: "
                                + request.getProductId());
            }
        }
    }



    ///cancel order////
    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {

        Order order = getOrder(orderId);

        validateOrderCancellation(order);

        releaseOrderInventory(order);

        order.setOrderStatus(OrderStatus.CANCELLED);

        Order updatedOrder = repository.save(order);
        OrderCancelledEvent event = OrderCancelledEvent.builder()
                .orderId(updatedOrder.getId())
                .orderNumber(updatedOrder.getOrderNumber())
                .reason("CUSTOMER_REQUEST")
                .eventTime(LocalDateTime.now())
                .build();
        orderEventProducer.publishOrderCancelled(event);
        return mapper.toResponse(updatedOrder);
    }
    private Order getOrder(Long orderId) {

        return repository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id : " + orderId,
                                ErrorCode.ORDER_NOT_FOUND));
    }
    private void validateOrderCancellation(Order order) {

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {

            throw new BusinessException(
                    "Order is already cancelled.",
                    ErrorCode.ORDER_ALREADY_CANCELLED);
        }

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {

            throw new BusinessException(
                    "Delivered order cannot be cancelled.",
                    ErrorCode.INVALID_ORDER_STATUS);
        }

        if (order.getOrderStatus() == OrderStatus.SHIPPED) {

            throw new BusinessException(
                    "Shipped order cannot be cancelled.",
                    ErrorCode.INVALID_ORDER_STATUS);
        }
    }
    private void releaseOrderInventory(Order order) {

        List<ReleaseStockRequest> requests =
                order.getOrderItems()
                        .stream()
                        .map(item -> ReleaseStockRequest.builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .build())
                        .toList();

        releaseReservedStock(requests);
    }
    @Override
    @Transactional
    public void deleteOrder(Long orderId) {

        Order order = getOrder(orderId);

        validateOrderDeletion(order);

        repository.delete(order);
    }
    private void validateOrderDeletion(Order order) {

        if (order.getOrderStatus() != OrderStatus.CANCELLED) {

            throw new BusinessException(
                    "Only cancelled orders can be deleted.",
                    ErrorCode.INVALID_ORDER_STATUS);
        }
    }
    @Override
    @Transactional
    public OrderResponse getOrderById(Long orderId) {

        Order order = getOrder(orderId);

        return mapper.toResponse(order);
    }

    @Override
    @Transactional
    public Page<OrderResponse> searchOrders(
            OrderSearchRequest request,
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort = buildSort(sortBy, direction);

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Order> specification =
                OrderSpecification.search(request);

        Page<Order> orderPage =
                repository.findAll(specification, pageable);

        return orderPage.map(mapper::toResponse);
    }
    private Sort buildSort(String sortBy, String direction) {

        validateSortField(sortBy);

        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        return Sort.by(sortDirection, sortBy);
    }
    private void validateSortField(String sortBy) {

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {

            throw new BusinessException(
                    "Invalid sort field: " + sortBy,
                    ErrorCode.INVALID_REQUEST);
        }
    }
}