package com.ecommerce.orderservice.dto.response;



import com.ecommerce.common.enums.PaymentMethod;
import com.ecommerce.orderservice.enums.OrderStatus;
import com.ecommerce.orderservice.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;

    private String orderNumber;

    private Long customerId;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;

    private PaymentMethod paymentMethod;

    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    private BigDecimal taxAmount;

    private BigDecimal finalAmount;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;

}
