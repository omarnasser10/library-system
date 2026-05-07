package com.library.controller;

import com.library.dto.UpdateUserRequest;
import com.library.dto.UserResponse;
import com.library.model.User;
import com.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> response = userService
                .getAllUsers()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        User user = userService.getUserById(id);

        return ResponseEntity.ok(
                toResponse(user)
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateUser(
            @Valid @RequestBody UpdateUserRequest request) {

        Long userId =
                (Long) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user = userService.updateProfile(
                userId,
                request.getPassword(),
                request.getName(),
                request.getEmail()
        );

        return ResponseEntity.ok(
                toResponse(user)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> viewProfile() {

        Long userId =
                (Long) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user = userService.viewProfile(userId);

        return ResponseEntity.ok(
                toResponse(user)
        );
    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}