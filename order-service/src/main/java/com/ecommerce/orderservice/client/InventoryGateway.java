package com.ecommerce.orderservice.client;

import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.orderservice.dto.client.ReserveStockRequest;
import com.ecommerce.orderservice.dto.response.InventoryProductResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryGateway {

    private final InventoryClient inventoryClient;

    // =========================================================================
    // GET PRODUCT
    // =========================================================================

    @RateLimiter(
            name = "inventoryService",
            fallbackMethod = "rateLimiterGetProductFallback"
    )
    @Bulkhead(
            name = "inventoryService",
            fallbackMethod = "bulkheadGetProductFallback"
    )
    @Retry(
            name = "inventoryService",
            fallbackMethod = "retryGetProductFallback"
    )
    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "circuitBreakerGetProductFallback"
    )
    public InventoryProductResponse getProduct(Long productId) {

        return inventoryClient.getProduct(productId).getData();
    }

    private InventoryProductResponse retryGetProductFallback(
            Long productId,
            Throwable ex) {

        log.warn(
                "Retry exhausted while fetching Product [{}].",
                productId,
                ex
        );

        throw new BusinessException(
                "Unable to fetch product after multiple retry attempts.",
                ErrorCode.INVENTORY_SERVICE_UNAVAILABLE
        );
    }

    private InventoryProductResponse circuitBreakerGetProductFallback(
            Long productId,
            Throwable ex) {

        log.error(
                "Circuit Breaker OPEN while fetching Product [{}].",
                productId,
                ex
        );

        throw new BusinessException(
                "Inventory Service is temporarily unavailable.",
                ErrorCode.INVENTORY_SERVICE_UNAVAILABLE
        );
    }

    private InventoryProductResponse bulkheadGetProductFallback(
            Long productId,
            Throwable ex) {

        log.error(
                "Bulkhead FULL while fetching Product [{}].",
                productId,
                ex
        );

        throw new BusinessException(
                "Inventory Service is busy. Please try again later.",
                ErrorCode.INVENTORY_SERVICE_UNAVAILABLE
        );
    }

    private InventoryProductResponse rateLimiterGetProductFallback(
            Long productId,
            Throwable ex) {

        log.error(
                "Rate limit exceeded while fetching Product [{}].",
                productId,
                ex
        );

        throw new BusinessException(
                "Too many requests. Please try again shortly.",
                ErrorCode.INVALID_REQUEST
        );
    }

    // =========================================================================
    // RESERVE STOCK
    // =========================================================================

    @RateLimiter(
            name = "inventoryService",
            fallbackMethod = "rateLimiterReserveStockFallback"
    )
    @Bulkhead(
            name = "inventoryService",
            fallbackMethod = "bulkheadReserveStockFallback"
    )
    @Retry(
            name = "inventoryService",
            fallbackMethod = "retryReserveStockFallback"
    )
    @CircuitBreaker(
            name = "inventoryService",
            fallbackMethod = "circuitBreakerReserveStockFallback"
    )
    public void reserveStock(ReserveStockRequest request) {

        inventoryClient.reserveStock(request);
    }

    private void retryReserveStockFallback(
            ReserveStockRequest request,
            Throwable ex) {

        log.warn(
                "Retry exhausted while reserving stock. ProductId={}, Quantity={}",
                request.getProductId(),
                request.getQuantity(),
                ex
        );

        throw new BusinessException(
                "Unable to reserve stock after multiple retry attempts.",
                ErrorCode.INVENTORY_SERVICE_UNAVAILABLE
        );
    }

    private void circuitBreakerReserveStockFallback(
            ReserveStockRequest request,
            Throwable ex) {

        log.error(
                "Circuit Breaker OPEN while reserving stock. ProductId={}, Quantity={}",
                request.getProductId(),
                request.getQuantity(),
                ex
        );

        throw new BusinessException(
                "Inventory Service is temporarily unavailable.",
                ErrorCode.INVENTORY_SERVICE_UNAVAILABLE
        );
    }

    private void bulkheadReserveStockFallback(
            ReserveStockRequest request,
            Throwable ex) {

        log.error(
                "Bulkhead FULL while reserving stock. ProductId={}, Quantity={}",
                request.getProductId(),
                request.getQuantity(),
                ex
        );

        throw new BusinessException(
                "Inventory Service is busy. Unable to reserve stock.",
                ErrorCode.INVENTORY_SERVICE_UNAVAILABLE
        );
    }

    private void rateLimiterReserveStockFallback(
            ReserveStockRequest request,
            Throwable ex) {

        log.error(
                "Rate limit exceeded while reserving stock. ProductId={}, Quantity={}",
                request.getProductId(),
                request.getQuantity(),
                ex
        );

        throw new BusinessException(
                "Too many requests. Please try again later.",
                ErrorCode.INVALID_REQUEST
        );
    }
}