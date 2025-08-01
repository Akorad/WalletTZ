package org.Akorad.service;

import org.Akorad.dto.response.UserResponse;
import org.Akorad.dto.security.RegisterRequest;
import org.Akorad.entity.User;

import java.util.Optional;

public interface UserService {

    UserResponse register (RegisterRequest request);

    Optional<UserResponse> findByUsername(String username);

    Optional<User> findUserByUsername(String username);

    User getCurrentUser();
}
