package com.library.service;

import com.library.exception.EmailAlreadyExistsException;
import com.library.exception.UserNotFoundException;
import com.library.model.Role;
import com.library.model.User;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder bcy;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Omar", "omar@test.com", "hashed", Role.MEMBER);
        user.setId(1L);
    }

    // =============================================
    // viewProfile
    // =============================================
    @Test
    void viewProfile_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.viewProfile(1L);

        assertThat(result.getEmail()).isEqualTo("omar@test.com");
    }

    @Test
    void viewProfile_throwsWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.viewProfile(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    // =============================================
    // updateProfile
    // =============================================
    @Test
    void updateProfile_updatesName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateProfile(1L, null, "New Name", null);

        assertThat(result.getName()).isEqualTo("New Name");
    }

    @Test
    void updateProfile_throwsWhenNewEmailAlreadyExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile(1L, null, null, "taken@test.com"))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void updateProfile_encodesNewPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bcy.encode("newPass123")).thenReturn("newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateProfile(1L, "newPass123", null, null);

        verify(bcy).encode("newPass123");
        assertThat(user.getPassword()).isEqualTo("newHashedPassword");
    }

    // =============================================
    // getAllUsers
    // =============================================
    @Test
    void getAllUsers_returnsPaginatedUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.getAllUsers(pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    // =============================================
    // getUserById
    // =============================================
    @Test
    void getUserById_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getUserById_throwsWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    // =============================================
    // deleteUser
    // =============================================
    @Test
    void deleteUser_deletesSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_throwsWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(UserNotFoundException.class);
    }
}
