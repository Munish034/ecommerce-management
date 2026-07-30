package com.ecommerce.common.security.filter;

import com.ecommerce.common.security.jwt.JwtService;
import com.ecommerce.common.security.model.JwtUser;
import com.ecommerce.common.security.util.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(SecurityConstants.AUTHORIZATION);

        if (authHeader == null ||
                !authHeader.startsWith(SecurityConstants.BEARER)) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authHeader.substring(SecurityConstants.BEARER.length());

        if (!jwtService.isTokenValid(token)) {

            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtService.extractUserId(token);

        String username =
                jwtService.extractDisplayUsername(token);

        List<String> roles =
                jwtService.extractRoles(token);

        JwtUser jwtUser = JwtUser.builder()
                .userId(userId)
                .username(username)
                .authorities(
                        roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList()
                )
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        jwtUser,
                        null,
                        jwtUser.getAuthorities());

        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request));

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}