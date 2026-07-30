package com.ecommerce.authservice.service.impl;

import com.ecommerce.authservice.entity.RefreshToken;
import com.ecommerce.authservice.repository.RefreshTokenRepository;
import com.ecommerce.authservice.service.RefreshTokenService;
import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${jwt.refresh-expiration-days}")
    private long refreshExpirationDays;

    @Override
    public RefreshToken createRefreshToken(Long userId) {

        repository.deleteByUserId(userId);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(userId)
                .expiryDate(LocalDateTime.now().plusDays(refreshExpirationDays))
                .revoked(false)
                .build();

        return repository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() ->
                        new BusinessException(
                                "Invalid refresh token.",
                                ErrorCode.INVALID_TOKEN
                        ));

        if (refreshToken.isRevoked()) {
            throw new BusinessException(
                    "Refresh token has been revoked.",
                    ErrorCode.INVALID_TOKEN
            );
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(
                    "Refresh token has expired.",
                    ErrorCode.TOKEN_EXPIRED
            );
        }

        return refreshToken;
    }

    @Override
    public void revokeRefreshToken(Long userId) {
        repository.deleteByUserId(userId);
    }
}