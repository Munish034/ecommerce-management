package com.ecommerce.common.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtUser {

    private Long userId;

    private String email;

    private String username;

    private Collection<? extends GrantedAuthority> authorities;

}