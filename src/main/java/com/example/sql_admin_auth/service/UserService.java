package com.example.sql_admin_auth.service;

import com.example.sql_admin_auth.entity.User;
import com.example.sql_admin_auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET all users
    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByIdDesc();
    }

    // POST a new user (admin or user role)
    public User createUser(User newUser) {
        // Hash the password before saving
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(newUser);
    }

    // DELETE a user by ID
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }
}