package com.ecommerce.cartservice.dto.request;

import com.ecommerce.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {

    @NotNull(message = "Shipping address ID is required")
    private Long shippingAddressId;

    @NotNull(message = "Payment Method is required")
    private PaymentMethod paymentMethod;
}