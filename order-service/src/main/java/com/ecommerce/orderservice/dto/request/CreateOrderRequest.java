package com.ecommerce.orderservice.dto.request;



import com.ecommerce.orderservice.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull(message = "Customer Id is required")
    private Long customerId;

    @NotNull(message = "Payment Method is required")
    private PaymentMethod paymentMethod;

    @Valid
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequest> items;

}