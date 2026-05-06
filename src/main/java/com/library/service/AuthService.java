package com.library.service;

import com.library.dto.LoginRequest;
import com.library.dto.LoginResponse;
import com.library.dto.RegisterRequest;
import com.library.model.Role;
import com.library.model.User;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcy;
    private final JwtService jwt;

    @Autowired
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bcy, JwtService jwt) {
        this.userRepository = userRepository;
        this.bcy = bcy;
        this.jwt = jwt;
    }

    public User registerNewUser(RegisterRequest request) {
        if (request.getName() == null || request.getEmail() == null || request.getPassword() == null) {
            throw new RuntimeException("Missing required fields");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        String hashed_password=bcy.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getEmail(),
                hashed_password,
                Role.MEMBER
        );

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request){
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new RuntimeException("Missing required fields");
        }
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new RuntimeException("User not found"));
        if(!bcy.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid password");
        return new LoginResponse(jwt.generateToken(user),user.getId(),user.getName(), user.getEmail(), user.getRole());
    }
}