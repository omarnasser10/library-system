package com.library.service;

import com.library.repository.UserRepository;
import com.library.model.User;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User viewProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public User updateProfile(Long userId,String newPassword,String newName,String newEmail){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (newName != null && !newName.equals(user.getName()))
            user.setName(newName);

        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail))
                throw new RuntimeException("Email already exists");
            user.setEmail(newEmail);
        }

        if (newPassword != null && !newPassword.equals(user.getPassword()))
            user.setPassword(newPassword);

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