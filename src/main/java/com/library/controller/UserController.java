package com.library.controller;

import com.library.dto.UpdateUserRequest;
import com.library.dto.UserResponse;
import com.library.model.User;
import com.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ======================================
    // GET /users?page=0&size=10
    // Get all users (paginated) — Admin only
    // ======================================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<UserResponse> response = userService
                .getAllUsers(pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(response);
    }

    // ======================================
    // GET /users/{id}
    // Get a specific user by ID — Admin only
    // ======================================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {

        User user = userService.getUserById(id);
        return ResponseEntity.ok(toResponse(user));
    }

    // ======================================
    // GET /users/me
    // View my own profile — Authenticated user
    // ======================================
    @GetMapping("/me")
    public ResponseEntity<UserResponse> viewProfile() {

        Long userId = getCurrentUserId();
        User user = userService.viewProfile(userId);
        return ResponseEntity.ok(toResponse(user));
    }

    // ======================================
    // PATCH /users/me
    // Update my own profile — Authenticated user
    // ======================================
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateUser(
            @Valid @RequestBody UpdateUserRequest request) {

        Long userId = getCurrentUserId();

        User user = userService.updateProfile(
                userId,
                request.getPassword(),
                request.getName(),
                request.getEmail()
        );

        return ResponseEntity.ok(toResponse(user));
    }

    // ======================================
    // DELETE /users/{id}
    // Delete a user — Admin only
    // ======================================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ======================================
    // Helper: Get current user's ID from SecurityContext
    // ======================================
    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // ======================================
    // Helper: User → UserResponse
    // ======================================
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}