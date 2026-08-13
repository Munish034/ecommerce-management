package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.dto.request.CheckoutRequest;
import com.ecommerce.cartservice.dto.response.CartResponse;

public interface CheckoutService {

    CartResponse checkout(CheckoutRequest request);
}