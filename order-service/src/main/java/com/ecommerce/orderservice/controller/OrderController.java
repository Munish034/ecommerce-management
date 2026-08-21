package com.ecommerce.orderservice.controller;


import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.orderservice.dto.request.CreateOrderRequest;
import com.ecommerce.orderservice.dto.request.OrderSearchRequest;
import com.ecommerce.orderservice.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.orderservice.dto.response.OrderResponse;
import com.ecommerce.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


@GetMapping("test")
public String test(){

    return "test";
}

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse response = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Order Created Successfully",
                        response));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long id) {

        OrderResponse response = orderService.cancelOrder(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order Cancelled Successfully",
                        response
                ));

    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @PathVariable Long id) {

        orderService.deleteOrder(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order deleted successfully.",
                        null
                ));
    }
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id) {

        OrderResponse response = orderService.getOrderById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order fetched successfully.",
                        response
                ));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        OrderResponse response =
                orderService.updateOrderStatus(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order status updated successfully.",
                        response
                )
        );
    }
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> searchOrders(

            OrderSearchRequest request,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "id") String sortBy,

            @RequestParam(defaultValue = "desc") String direction) {

        Page<OrderResponse> response =
                orderService.searchOrders(
                        request,
                        page,
                        size,
                        sortBy,
                        direction);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Orders fetched successfully.",
                        response));
    }
}
