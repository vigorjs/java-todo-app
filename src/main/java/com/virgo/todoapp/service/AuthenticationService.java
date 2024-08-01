package com.virgo.todoapp.service;

import com.virgo.todoapp.entity.meta.User;
import com.virgo.todoapp.utils.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public interface AuthenticationService {

    public RegisterResponseDTO register(RegisterRequestDTO request);

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    Map<String, String> refresh(RefreshRequestDTO refreshToken);

    public User getUserAuthenticated();
}
