package org.Akorad.service;

import org.Akorad.dto.response.JwtResponse;
import org.Akorad.dto.response.UserResponse;
import org.Akorad.dto.security.LoginRequest;
import org.Akorad.dto.security.RegisterRequest;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
}
