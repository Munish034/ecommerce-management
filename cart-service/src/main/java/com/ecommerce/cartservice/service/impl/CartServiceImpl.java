package com.ecommerce.cartservice.service.impl;

import com.ecommerce.cartservice.client.InventoryClient;
import com.ecommerce.cartservice.dto.request.AddToCartRequest;
import com.ecommerce.cartservice.dto.request.UpdateCartItemRequest;
import com.ecommerce.cartservice.dto.response.CartResponse;
import com.ecommerce.cartservice.dto.response.InventoryProductResponse;
import com.ecommerce.cartservice.dto.response.ProductResponse;
import com.ecommerce.cartservice.entity.Cart;
import com.ecommerce.cartservice.entity.CartItem;
import com.ecommerce.cartservice.mapper.CartMapper;
import com.ecommerce.cartservice.repository.CartItemRepository;
import com.ecommerce.cartservice.repository.CartRepository;
import com.ecommerce.cartservice.service.CartService;
import com.ecommerce.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final InventoryClient inventoryClient;
    private final CartMapper cartMapper;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart() {

        Long customerId = SecurityUtils.getCurrentUserId();

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> createCart(customerId));

        return cartMapper.toResponse(cart);
    }

    @Override
    public CartResponse addToCart(AddToCartRequest request) {

        Long customerId = SecurityUtils.getCurrentUserId();

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> createCart(customerId));


        CartItem item =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                request.getProductId()
                        )
                        .orElse(null);
        InventoryProductResponse<ProductResponse> response = inventoryClient.getProduct(request.getProductId());
        ProductResponse product = response.getData();
        if (product == null || product.getPrice() == null) {

        }
        if (item == null) {

         item = CartItem.builder()
                    .cart(cart)
                    .productId(product.getId())
                    .quantity(request.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                    .build();

            cart.addItem(item);

        } else {

            int newQuantity =
                    item.getQuantity() + request.getQuantity();

            item.setQuantity(newQuantity);

            item.setUnitPrice(product.getPrice());

            item.setTotalPrice(
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(newQuantity)
                            )
            );
        }

        recalculateCart(cart);

        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toResponse(savedCart);
    }

    @Override
    public CartResponse updateCartItem(
            Long productId,
            UpdateCartItemRequest request) {

        Long customerId = SecurityUtils.getCurrentUserId();

        Cart cart = getCustomerCart(customerId);

        CartItem item =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                productId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found in cart."
                                ));

        InventoryProductResponse<ProductResponse> response = inventoryClient.getProduct(productId);
        ProductResponse product = response.getData();

        if (product == null || product.getPrice() == null) {

        }

        item.setQuantity(request.getQuantity());
        item.setUnitPrice(product.getPrice());

        item.setTotalPrice(
                product.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        request.getQuantity()
                                )
                        )
        );

        recalculateCart(cart);

        return cartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public void removeCartItem(Long productId) {

        Long customerId = SecurityUtils.getCurrentUserId();

        Cart cart = getCustomerCart(customerId);

        CartItem item =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                productId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found in cart."
                                ));

        cart.removeItem(item);

        recalculateCart(cart);

        cartRepository.save(cart);
    }

    @Override
    public void clearCart() {

        Long customerId = SecurityUtils.getCurrentUserId();

        Cart cart = getCustomerCart(customerId);

        cart.getItems().clear();

        cart.setTotalAmount(BigDecimal.ZERO);

        cartRepository.save(cart);
    }

    private Cart getCustomerCart(Long customerId) {

        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cart not found."
                        ));
    }

    private Cart createCart(Long customerId) {

        Cart cart = Cart.builder()
                .customerId(customerId)
                .totalAmount(BigDecimal.ZERO)
                .build();

        return cartRepository.save(cart);
    }

    private void recalculateCart(Cart cart) {

        BigDecimal total =
                cart.getItems()
                        .stream()
                        .map(CartItem::getTotalPrice)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        cart.setTotalAmount(total);
    }
}