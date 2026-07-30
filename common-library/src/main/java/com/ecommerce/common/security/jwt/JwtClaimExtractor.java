package com.ecommerce.common.security.jwt;

import io.jsonwebtoken.Claims;

public interface JwtClaimExtractor {

    Claims extractClaims(String token);

    String extractEmail(String token);

    Long extractUserId(String token);

}