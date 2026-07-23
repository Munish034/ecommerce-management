package com.ecommerce.orderservice.config;

import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RetryEventConfig {

    private final RetryRegistry retryRegistry;

    @PostConstruct
    public void registerRetryEvents() {

        retryRegistry.retry("inventoryService")
                .getEventPublisher()
                .onRetry(event ->

                        log.warn(
                                "Retry Attempt #{} for {}",
                                event.getNumberOfRetryAttempts(),
                                event.getName()
                        ));
    }
}