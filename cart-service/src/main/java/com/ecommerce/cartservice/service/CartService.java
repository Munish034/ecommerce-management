package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.dto.request.AddToCartRequest;
import com.ecommerce.cartservice.dto.request.UpdateCartItemRequest;
import com.ecommerce.cartservice.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart();

    CartResponse addToCart(AddToCartRequest request);

    CartResponse updateCartItem(
            Long productId,
            UpdateCartItemRequest request
    );

    void removeCartItem(Long productId);

    void clearCart();
}