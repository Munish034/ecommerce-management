package com.ecommerce.apigateway.filter;

import com.ecommerce.apigateway.constants.GatewayConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String traceId = exchange.getRequest()
                .getHeaders()
                .getFirst(GatewayConstants.TRACE_ID);

        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header(GatewayConstants.TRACE_ID, traceId)
                .build();

        long startTime = System.currentTimeMillis();

        log.info("[{}] Incoming Request : {} {}",
                traceId,
                request.getMethod(),
                request.getURI());

        String finalTraceId = traceId;
        return chain.filter(exchange.mutate().request(request).build())
                .doFinally(signal -> {

                    long executionTime = System.currentTimeMillis() - startTime;

                    log.info("[{}] Response Status : {} | Time : {} ms",
                            finalTraceId,
                            exchange.getResponse().getStatusCode(),
                            executionTime);
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}