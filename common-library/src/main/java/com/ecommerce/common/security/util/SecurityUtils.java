package com.ecommerce.common.security.util;

import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.security.model.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static JwtUser getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {

            return null;
        }

        return (JwtUser) authentication.getPrincipal();
    }

    public static Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUser jwtUser)) {
            throw new BusinessException("User is not authenticated", ErrorCode.USER_NOT_AUTHENTICATED);
        }

        return jwtUser.getUserId();
    }

    public static String getCurrentUsername() {

        JwtUser user = getCurrentUser();

        return user != null ? user.getUsername() : null;
    }

    public static boolean isAuthenticated() {

        return getCurrentUser() != null;
    }
}