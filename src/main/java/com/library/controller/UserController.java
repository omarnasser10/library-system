package com.library.controller;

import com.library.dto.UpdateUserRequest;
import com.library.dto.UserResponse;
import com.library.model.User;
import com.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final  UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponse> getAllUsers(){
        List<User> users=userService.getAllUsers();
        return users.stream().map(
                user ->new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                )
        ).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id){
        User user=userService.getUserById(id);
        return new UserResponse(user.getId(),user.getName(),user.getEmail());
    }

    @PatchMapping("/me")
    public UserResponse updateUser( @Valid @RequestBody UpdateUserRequest request){
        Long userId =
                (Long) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
        User user=userService.updateProfile(userId, request.getPassword(),request.getName(),request.getEmail());
        return new UserResponse(user.getId(),user.getName(),user.getEmail());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
}
