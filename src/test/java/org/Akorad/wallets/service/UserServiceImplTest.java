package org.Akorad.wallets.service;

import org.Akorad.dto.response.UserResponse;
import org.Akorad.dto.security.RegisterRequest;
import org.Akorad.dto.user.UserMapper;
import org.Akorad.entity.User;
import org.Akorad.exception.user.UserAlreadyExistsException;
import org.Akorad.reposetory.UserRepository;
import org.Akorad.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testUser");
        registerRequest.setPassword("password");

        user = new User();
        user.setUsername("testUser");
        user.setPassword("password");

        userResponse = new UserResponse();
        userResponse.setUsername("testUser");
    }

    @Test
    void register_ShouldSaveUser_WhenUsernameIsUnique(){
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodePassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse response = userService.register(registerRequest);

        assertNotNull(response);
        assertEquals("testUser",response.getUsername());

        verify(userRepository).existsByUsername("testUser");
        verify(userMapper).toEntity(registerRequest);
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists(){
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, ()-> userService.register(registerRequest));

        verify(userRepository).existsByUsername("testUser");
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }
}
