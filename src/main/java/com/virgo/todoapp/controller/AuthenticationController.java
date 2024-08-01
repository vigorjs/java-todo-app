package com.virgo.todoapp.controller;

import com.virgo.todoapp.utils.dto.*;
import com.virgo.todoapp.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RestControllerAdvice
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponseDTO register(@RequestBody @Valid RegisterRequestDTO request) {
        return service.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponseDTO authenticate(@RequestBody @Valid AuthenticationRequestDTO request) {
        return service.authenticate(request);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.refreshToken(request, response);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> refreshToken(@RequestBody @Valid RefreshRequestDTO request) {
        return service.refresh(request);
    }
}
