package com.library.service;

import com.library.dto.LoginRequest;
import com.library.dto.LoginResponse;
import com.library.dto.RegisterRequest;
import com.library.exception.EmailAlreadyExistsException;
import com.library.exception.InvalidCredentialsException;
import com.library.model.Role;
import com.library.model.User;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bcy;

    @Mock
    private JwtService jwt;

    @InjectMocks
    private AuthService authService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User("Omar", "omar@test.com", "hashedPassword", Role.MEMBER);
        existingUser.setId(1L);
    }

    // =============================================
    // registerNewUser
    // =============================================
    @Test
    void register_savesUserWhenEmailIsNew() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Omar");
        request.setEmail("omar@test.com");
        request.setPassword("123456");

        when(userRepository.existsByEmail("omar@test.com")).thenReturn(false);
        when(bcy.encode("123456")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = authService.registerNewUser(request);

        assertThat(result.getEmail()).isEqualTo("omar@test.com");
        assertThat(result.getRole()).isEqualTo(Role.MEMBER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("omar@test.com");

        when(userRepository.existsByEmail("omar@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerNewUser(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email already exists");
    }

    // =============================================
    // login
    // =============================================
    @Test
    void login_returnsTokenWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("omar@test.com");
        request.setPassword("123456");

        when(userRepository.findByEmail("omar@test.com")).thenReturn(Optional.of(existingUser));
        when(bcy.matches("123456", "hashedPassword")).thenReturn(true);
        when(jwt.generateToken(existingUser)).thenReturn("jwt-token-here");

        LoginResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token-here");
        assertThat(response.getEmail()).isEqualTo("omar@test.com");
    }

    @Test
    void login_throwsWhenEmailNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("wrong@test.com");
        request.setPassword("123456");

        when(userRepository.findByEmail("wrong@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void login_throwsWhenPasswordIsWrong() {
        LoginRequest request = new LoginRequest();
        request.setEmail("omar@test.com");
        request.setPassword("wrongPassword");

        when(userRepository.findByEmail("omar@test.com")).thenReturn(Optional.of(existingUser));
        when(bcy.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }
}
