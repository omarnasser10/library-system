package com.library.service;

import com.library.exception.EmailAlreadyExistsException;
import com.library.exception.UserNotFoundException;
import com.library.repository.UserRepository;
import com.library.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcy;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bcy) {
        this.userRepository = userRepository;
        this.bcy = bcy;
    }

    public User viewProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public User updateProfile(
            Long userId,
            String newPassword,
            String newName,
            String newEmail){

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (newName != null && !newName.isBlank()) {
            user.setName(newName);
        }

        if (newEmail != null && !newEmail.equals(user.getEmail())) {

            if (userRepository.existsByEmail(newEmail)) {
                throw new EmailAlreadyExistsException(
                        "Email already exists"
                );
            }

            user.setEmail(newEmail);
        }

        if (newPassword != null && !newPassword.isBlank()) {

            user.setPassword(
                    bcy.encode(newPassword)
            );
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    public void deleteUser(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
}