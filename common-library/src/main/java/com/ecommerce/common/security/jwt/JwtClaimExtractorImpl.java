package com.ecommerce.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JwtClaimExtractorImpl
        implements JwtClaimExtractor {

    private final SecretKey signingKey;

    public JwtClaimExtractorImpl(String secret) {

        byte[] bytes = Decoders.BASE64.decode(secret);

        this.signingKey = Keys.hmacShaKeyFor(bytes);
    }

    @Override
    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public String extractEmail(String token) {

        return extractClaims(token).getSubject();
    }

    @Override
    public Long extractUserId(String token) {

        Number id = extractClaims(token)
                .get("userId", Number.class);

        return id.longValue();
    }
}