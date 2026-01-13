package com.example.sql_admin_auth.service;

import com.example.sql_admin_auth.dto.SignupRequest;
import com.example.sql_admin_auth.dto.UserDTO;
import com.example.sql_admin_auth.entity.Role; // Ensure Role Enum exists
import com.example.sql_admin_auth.entity.User;
import com.example.sql_admin_auth.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    // --- ADMIN METHODS (Return Entities to match ManagementController) ---

    // GET all users
    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByIdDesc();
    }

    // POST a new user (Internal Admin use)
    public User createUser(User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        // If role is missing, default to USER
        if (newUser.getRole() == null) {
            newUser.setRole(Role.USER);
        }
        return userRepository.save(newUser);
    }

    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    // --- PUBLIC AUTH METHODS (Return DTOs to match AuthController) ---

    public UserDTO registerUser(SignupRequest request) {
        // 1. Check Username
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }
        // 2. Check Email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already taken!");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setCourse(request.getCourse());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set Role using Enum
        newUser.setRole(Role.USER);

        User savedUser = userRepository.save(newUser);

        // Convert to DTO safely
        return modelMapper.map(savedUser, UserDTO.class);
    }
}