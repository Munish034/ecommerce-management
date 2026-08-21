package com.ecommerce.orderservice.service.imp;

import com.ecommerce.common.enums.CancellationReason;
import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.events.OrderCancelledEvent;
import com.ecommerce.common.events.OrderCreatedEvent;
import com.ecommerce.common.events.OrderStatusChangedEvent;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.common.security.util.SecurityUtils;
import com.ecommerce.orderservice.client.InventoryGateway;
import com.ecommerce.orderservice.client.PaymentClient;
import com.ecommerce.orderservice.dto.client.PaymentRequest;
import com.ecommerce.orderservice.dto.client.PaymentResponse;
import com.ecommerce.orderservice.dto.client.ReleaseStockRequest;
import com.ecommerce.orderservice.dto.client.ReserveStockRequest;
import com.ecommerce.orderservice.dto.request.CreateOrderRequest;
import com.ecommerce.orderservice.dto.request.OrderSearchRequest;
import com.ecommerce.orderservice.dto.request.UpdateOrderStatusRequest;
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
import com.ecommerce.orderservice.service.OrderTransactionService;
import com.ecommerce.orderservice.service.OutboxService;
import com.ecommerce.orderservice.service.PricingService;
import com.ecommerce.orderservice.specification.OrderSpecification;
import com.ecommerce.orderservice.util.OrderNumberGenerator;
import com.ecommerce.orderservice.client.InventoryClient;
import com.ecommerce.orderservice.enums.PaymentStatus;
import org.springframework.transaction.annotation.Transactional;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
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
    private final OutboxService outboxService;
    private final OrderTransactionService transactionService;




    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "orderNumber",
            "orderStatus",
            "paymentStatus",
            "totalAmount",
            "finalAmount",
            "createdAt"
    );

     /////create order /////

     @Override
     @Transactional
     public OrderResponse createOrder(CreateOrderRequest request) {

         Order order = buildOrder(request);

         // Save the order first so Saga has something to compensate
         Order savedOrder = transactionService.savePendingOrder(order);

         List<ReleaseStockRequest> reservedItems = new ArrayList<>();

         try {

             reserveInventory(savedOrder, reservedItems);

             pricingService.calculatePrice(savedOrder);

             processPayment(savedOrder);

             savedOrder.setOrderStatus(OrderStatus.CONFIRMED);
             savedOrder.setPaymentStatus(PaymentStatus.PAID);

             savedOrder = repository.save(savedOrder);

             transactionService.confirmOrder(savedOrder);

             OrderCreatedEvent event = OrderCreatedEvent.builder()
                     .orderId(savedOrder.getId())
                     .orderNumber(savedOrder.getOrderNumber())
                     .totalAmount(savedOrder.getFinalAmount())
                     .paymentMethod(savedOrder.getPaymentMethod())
                     .eventTime(LocalDateTime.now())
                     .build();

             outboxService.saveEvent(
                     "ORDER",
                     savedOrder.getId(),
                     "ORDER_CREATED",
                     event
             );

             return mapper.toResponse(savedOrder);

         } catch (Exception ex) {

             log.error(
                     "Order processing failed for order [{}]. Waiting for Saga compensation if payment failed.",
                     savedOrder.getOrderNumber(),
                     ex
             );

             throw ex;
         }
     }
    @Override
    @Transactional
    public void cancelOrderByOrderNumber(String orderNumber) {

        Order order = repository.findByOrderNumber(orderNumber)
                .orElse(null);

        if (order == null) {
            log.warn(
                    "Order [{}] not found. Ignoring stale PaymentFailedEvent.",
                    orderNumber
            );
            return;
        }

        // Kafka events can be delivered more than once.
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {

            log.info(
                    "Order [{}] is already cancelled. Skipping duplicate PaymentFailedEvent.",
                    orderNumber
            );

            return;
        }

        log.info(
                "Cancelling order [{}] because payment failed.",
                orderNumber
        );

        // Release reserved inventory
        releaseOrderInventory(order);

        // Update order status
        order.setOrderStatus(OrderStatus.CANCELLED);

        // Payment failed
        order.setPaymentStatus(PaymentStatus.FAILED);

        Order updatedOrder = repository.save(order);

        OrderCancelledEvent event =
                OrderCancelledEvent.builder()
                        .orderId(updatedOrder.getId())
                        .orderNumber(updatedOrder.getOrderNumber())
                        .reason(CancellationReason.PAYMENT_FAILED.name())
                        .eventTime(LocalDateTime.now())
                        .build();

        outboxService.saveEvent(
                "ORDER",
                updatedOrder.getId(),
                "ORDER_CANCELLED",
                event
        );

        log.info(
                "Order [{}] cancelled successfully after payment failure.",
                orderNumber
        );
    }

    private Order buildOrder(CreateOrderRequest request) {

        Order order = mapper.toEntity(request);

        order.setCustomerId(SecurityUtils.getCurrentUserId());

        order.setOrderNumber(generator.generateOrderNumber());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

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
                                .customerId(order.getCustomerId())
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

            log.info(
                    "Releasing inventory. Product ID: {}, Quantity: {}",
                    request.getProductId(),
                    request.getQuantity()
            );

            inventoryClient.releaseStock(request);

            log.info(
                    "Inventory released successfully. Product ID: {}, Quantity: {}",
                    request.getProductId(),
                    request.getQuantity()
            );
        }
    }

    ///cancel order////
    @Override
    @Transactional

    public OrderResponse cancelOrder(Long orderId, String reason) {

        Order order = getOrder(orderId);
        validateOrderOwnership(order);
        validateOrderCancellation(order);

        releaseOrderInventory(order);

        order.setOrderStatus(OrderStatus.CANCELLED);

        Order updatedOrder = repository.save(order);

        OrderCancelledEvent event = OrderCancelledEvent.builder()
                .orderId(updatedOrder.getId())
                .orderNumber(updatedOrder.getOrderNumber())

                .reason(reason)
                .eventTime(LocalDateTime.now())
                .build();

        outboxService.saveEvent(
                "ORDER",
                updatedOrder.getId(),
                "ORDER_CANCELLED",
                event
        );

        return mapper.toResponse(updatedOrder);
    }
    @Override
    public OrderResponse cancelOrder(Long orderId) {

        return cancelOrder(
                orderId,
                CancellationReason.CUSTOMER_REQUEST.name()
        );

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

        log.info(
                "Starting inventory release for order [{}]",
                order.getOrderNumber()
        );

        List<ReleaseStockRequest> requests =
                order.getOrderItems()
                        .stream()
                        .map(item -> ReleaseStockRequest.builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .build())
                        .toList();

        if (requests.isEmpty()) {
            log.warn(
                    "No order items found for order [{}]. Inventory cannot be released.",
                    order.getOrderNumber()
            );
            return;
        }

        releaseReservedStock(requests);

        log.info(
                "Inventory release completed for order [{}]",
                order.getOrderNumber()
        );
    }
    @Override
    @Transactional
    public void deleteOrder(Long orderId) {

        Order order = getOrder(orderId);

        validateOrderOwnership(order);

        if (order.getOrderStatus() != OrderStatus.CANCELLED) {
            throw new BusinessException(
                    "Only cancelled orders can be deleted.",
                    ErrorCode.INVALID_ORDER_STATUS
            );
        }



        repository.save(order);
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
        validateOrderOwnership(order);
        return mapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrders(
            OrderSearchRequest request,
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort = buildSort(sortBy, direction);

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Specification<Order> specification =
                OrderSpecification.search(request);

        boolean isAdmin =
                SecurityUtils.hasRole("ADMIN");

        if (!isAdmin) {

            Long currentUserId =
                    SecurityUtils.getCurrentUserId();

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("customerId"),
                                    currentUserId
                            )
            );
        }

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
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequest request) {

        Order order = getOrder(orderId);

        OrderStatus previousStatus =
                order.getOrderStatus();

        OrderStatus newStatus =
                request.getOrderStatus();

        validateOrderStatusTransition(
                previousStatus,
                newStatus
        );

        order.setOrderStatus(newStatus);

        Order updatedOrder =
                repository.save(order);

        OrderStatusChangedEvent event =
                OrderStatusChangedEvent.builder()
                        .orderId(updatedOrder.getId())
                        .orderNumber(updatedOrder.getOrderNumber())
                        .previousStatus(previousStatus.name())
                        .newStatus(newStatus.name())
                        .eventTime(LocalDateTime.now())
                        .build();

        outboxService.saveEvent(
                "ORDER",
                updatedOrder.getId(),
                "ORDER_STATUS_CHANGED",
                event
        );

        return mapper.toResponse(updatedOrder);
    }
    private void validateOrderStatusTransition(
            OrderStatus currentStatus,
            OrderStatus newStatus) {

        if (currentStatus == newStatus) {
            throw new BusinessException(
                    "Order is already in " + newStatus + " status.",
                    ErrorCode.INVALID_ORDER_STATUS
            );
        }

        boolean validTransition =
                switch (currentStatus) {

                    case CREATED ->
                            newStatus == OrderStatus.CONFIRMED
                                    || newStatus == OrderStatus.CANCELLED;

                    case CONFIRMED ->
                            newStatus == OrderStatus.PROCESSING
                                    || newStatus == OrderStatus.CANCELLED;

                    case PROCESSING ->
                            newStatus == OrderStatus.SHIPPED
                                    || newStatus == OrderStatus.CANCELLED;

                    case SHIPPED ->
                            newStatus == OrderStatus.DELIVERED;

                    case DELIVERED, CANCELLED ->
                            false;

                    default ->
                            false;
                };

        if (!validTransition) {
            throw new BusinessException(
                    "Invalid order status transition from "
                            + currentStatus
                            + " to "
                            + newStatus,
                    ErrorCode.INVALID_ORDER_STATUS
            );
        }
    }
    private void validateOrderOwnership(Order order) {

        if (SecurityUtils.hasRole("ADMIN")) {
            return;
        }

        Long currentUserId =
                SecurityUtils.getCurrentUserId();

        if (!order.getCustomerId().equals(currentUserId)) {
            throw new AccessDeniedException(
                    "You are not authorized to cancel this order."
            );
        }
    }
}