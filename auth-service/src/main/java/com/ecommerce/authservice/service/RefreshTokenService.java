package com.ecommerce.authservice.service;

import com.ecommerce.authservice.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(Long userId);

    RefreshToken verifyRefreshToken(String token);

    void revokeRefreshToken(Long userId);
}