package com.library.controller;

import com.library.dto.UpdateUserRequest;
import com.library.dto.UserResponse;
import com.library.model.User;
import com.library.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final  UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id){
        User user=userService.getUserById(id);
        return new UserResponse(user.getId(),user.getName(),user.getEmail());
    }

    @PatchMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,@RequestBody UpdateUserRequest request){
        User user=userService.updateProfile(id, request.getPassword(),request.getName(),request.getEmail());
        return new UserResponse(user.getId(),user.getName(),user.getEmail());
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
}
