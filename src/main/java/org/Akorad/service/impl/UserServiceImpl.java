package org.Akorad.service.impl;

import lombok.RequiredArgsConstructor;
import org.Akorad.dto.response.UserResponse;
import org.Akorad.dto.security.RegisterRequest;
import org.Akorad.dto.user.UserMapper;
import org.Akorad.entity.User;
import org.Akorad.exception.user.UserAlreadyExistsException;
import org.Akorad.exception.user.UserNotFoundException;
import org.Akorad.reposetory.UserRepository;
import org.Akorad.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(request.getUsername());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public Optional<UserResponse> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toResponse);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}
