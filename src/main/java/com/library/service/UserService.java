package com.library.service;

import com.library.exception.EmailAlreadyExistsException;
import com.library.exception.UserNotFoundException;
import com.library.model.Role;
import com.library.model.User;
import com.library.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcy;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bcy) {
        this.userRepository = userRepository;
        this.bcy = bcy;
    }

    // ============================
    // View Profile
    // ============================
    public User viewProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    // ============================
    // Update Profile
    // ============================
    public User updateProfile(Long userId, String newPassword, String newName, String newEmail) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (newName != null && !newName.isBlank()) {
            user.setName(newName);
        }

        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new EmailAlreadyExistsException("Email already exists");
            }
            user.setEmail(newEmail);
        }

        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(bcy.encode(newPassword));
        }

        return userRepository.save(user);
    }

    // ============================
    // Get All Users (with pagination)
    // ============================
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findByRoleNot(Role.ADMIN, pageable);
    }

    // ============================
    // Get User By ID
    // ============================
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    // ============================
    // Delete User
    // ============================
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
    }
}
