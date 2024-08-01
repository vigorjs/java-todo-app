package com.virgo.todoapp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virgo.todoapp.config.JwtService;
import com.virgo.todoapp.config.advisers.exception.AuthenticationException;
import com.virgo.todoapp.config.advisers.exception.ConflictException;
import com.virgo.todoapp.config.advisers.exception.NotFoundException;
import com.virgo.todoapp.utils.dto.*;
import com.virgo.todoapp.entity.meta.Token;
import com.virgo.todoapp.entity.meta.User;
import com.virgo.todoapp.entity.enums.Role;
import com.virgo.todoapp.entity.enums.TokenType;
import com.virgo.todoapp.repo.TokenRepository;
import com.virgo.todoapp.repo.UserRepository;
import com.virgo.todoapp.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        if (repository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .mobileNumber(request.getMobileNumber())
                .gender(request.getGender() != null ? request.getGender() : null)
                .role(Role.USER)
                .build();
        repository.save(user);

        return RegisterResponseDTO.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .build();
    }

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(()-> new AuthenticationException("invalid credentials"));
        if ( !passwordEncoder.matches(request.getPassword(), user.getPassword()) ){
            throw new AuthenticationException("invalid credentials");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponseDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    public Map<String, String> refresh(RefreshRequestDTO refreshToken) {
        try {
            final String userEmail = jwtService.extractUsername(refreshToken.getRefreshToken());
            if (userEmail != null) {
                var user = this.repository.findByEmail(userEmail).orElseThrow(() ->
                        new AuthenticationException("refresh token invalid credentials"));
                if (jwtService.isTokenValid(refreshToken.getRefreshToken(), user)) {
                    String accessToken = jwtService.generateToken(user);
                    return Map.of("accessToken", accessToken);
                }
            }
        } catch (Exception e) {
            throw new AuthenticationException("invalid refresh token credentials");
        }
        throw new AuthenticationException("invalid refresh token credentials");
    }

    @Override
    public User getUserAuthenticated() {
        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication1.getName()).orElseThrow(() -> new AuthenticationException("Unauthorized, silahkan login"));
        return user;
    }
}
