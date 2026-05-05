package com.library.service;

import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.model.Role;
import com.library.model.User;
import com.library.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepository;


    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerNewUser(RegisterRequest request) {
        if (request.getName() == null || request.getEmail() == null || request.getPassword() == null) {
            throw new RuntimeException("Missing required fields");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                Role.MEMBER
        );

        return userRepository.save(user);
    }

    public User login(LoginRequest request){
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new RuntimeException("Missing required fields");
        }
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new RuntimeException("User not found"));
        if(!(request.getPassword().equals(user.getPassword())))
            throw new RuntimeException("Invalid password");
        return user;
    }
}