package com.ecommerce.authservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

    @Override
    public Long getCurrentUserId() {
        return getPrincipal().getUser().getId();
    }

    @Override
    public String getCurrentUserEmail() {
        return getPrincipal().getUsername();
    }

    @Override
    public String getCurrentUsername() {
        return getPrincipal().getUser().getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getPrincipal().getAuthorities();
    }

    @Override
    public boolean hasRole(String role) {
        return getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    private CustomUserDetails getPrincipal() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            throw new IllegalStateException("No authenticated user found.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("Invalid authenticated principal.");
        }

        return userDetails;
    }
}