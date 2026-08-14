package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.dto.request.CheckoutRequest;
import com.ecommerce.cartservice.dto.response.CartResponse;
import com.ecommerce.cartservice.dto.response.CheckoutResponse;

public interface CheckoutService {

    CheckoutResponse checkout(CheckoutRequest request);
}