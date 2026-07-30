package com.ecommerce.authservice.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface CurrentUserService {

    Long getCurrentUserId();

    String getCurrentUserEmail();

    String getCurrentUsername();

    Collection<? extends GrantedAuthority> getAuthorities();

    boolean hasRole(String role);
}