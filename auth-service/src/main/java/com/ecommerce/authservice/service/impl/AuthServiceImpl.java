package com.ecommerce.authservice.service.impl;

import com.ecommerce.authservice.dto.request.LoginRequest;
import com.ecommerce.authservice.dto.request.RefreshTokenRequest;
import com.ecommerce.authservice.dto.request.RegisterRequest;
import com.ecommerce.authservice.dto.response.LoginResponse;
import com.ecommerce.authservice.dto.response.RegisterResponse;
import com.ecommerce.authservice.entity.RefreshToken;
import com.ecommerce.authservice.entity.Role;
import com.ecommerce.authservice.entity.User;
import com.ecommerce.authservice.enums.RoleName;
import com.ecommerce.authservice.mapper.AuthMapper;
import com.ecommerce.authservice.repository.RoleRepository;
import com.ecommerce.authservice.repository.UserRepository;
import com.ecommerce.authservice.security.CustomUserDetails;
import com.ecommerce.authservice.security.CustomUserDetailsService;
import com.ecommerce.authservice.security.jwt.JwAuthService;
import com.ecommerce.authservice.service.AuthService;
import com.ecommerce.authservice.service.RefreshTokenService;
import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwAuthService jwtService;
    private final AuthMapper authMapper;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email is already registered.", ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        ///Fetch the default role.//
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Default role not found.",ErrorCode.ROLE_NOT_FOUND));
        User user = authMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        ///default role
        user.getRoles().add(userRole);

        ///save user
        User savedUser = userRepository.save(user);

        ///Generate JWT
        return RegisterResponse.builder()
                .id(savedUser.getId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(savedUser.getEmail())
                .message("User registered successfully.")
                .build();

    }
////login ////
    @Override
    public LoginResponse login(LoginRequest request) {

        ///authentication
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        ));

        ///load user from db
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found."));
        ///custom_user details
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        ///generate jwt token
        String token = jwtService.generateToken(userDetails);

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user.getId());

        return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.rotateRefreshToken(
                        request.getRefreshToken());
        System.out.println( "refresh token "+request.getRefreshToken());

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found",
                                ErrorCode.USER_NOT_FOUND));


        CustomUserDetails userDetails =
                (CustomUserDetails) userDetailsService
                        .loadUserByUsername(user.getEmail());

        System.out.println(refreshToken.getUserId()+"====="+(user.getEmail()));
        String accessToken =
                jwtService.generateToken(userDetails);



        return LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
    @Override
    public void logout(String refreshToken) {

        refreshTokenService.revokeRefreshToken(refreshToken);

    }
}