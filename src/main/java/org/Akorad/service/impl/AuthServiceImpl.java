package org.Akorad.service.impl;

import lombok.RequiredArgsConstructor;
import org.Akorad.dto.response.JwtResponse;
import org.Akorad.dto.response.UserResponse;
import org.Akorad.dto.security.LoginRequest;
import org.Akorad.dto.security.RegisterRequest;
import org.Akorad.security.JwtTokenProvider;
import org.Akorad.service.AuthService;
import org.Akorad.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager AuthenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserResponse register(RegisterRequest request) {
        return userService.register(request);
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication auth;
        try {
            auth = AuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Неверный логин или пароль");
        }

        String token = jwtTokenProvider.generateToken(request.getUsername());

        return new JwtResponse(token);
    }
}
