package com.example.sql_admin_auth.service;

import com.example.sql_admin_auth.dto.SignupRequest;
import com.example.sql_admin_auth.dto.UserDTO;
import com.example.sql_admin_auth.entity.Role;
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

    // --- ADMIN METHODS (Used by ManagementController) ---

    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByIdDesc();
    }

    // POST a new user (Internal Admin use)
    public User createUser(User newUser) {
        // 1. Encode the password before saving
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        // 2. Set default role if missing
        if (newUser.getRole() == null) {
            newUser.setRole(Role.USER);
        }

        // 3. Set default notification preferences (optional, but good practice)
        // (boolean fields default to false automatically, so this is implicit)

        return userRepository.save(newUser);
    }

    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    // --- PUBLIC AUTH METHODS (Used by AuthController) ---

    public UserDTO registerUser(SignupRequest request) {
        // 1. Check Username & Email
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already taken!");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setCourse(request.getCourse());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        // 2. Set Role
        newUser.setRole(Role.USER);

        // 🛑 FIX: Map Notification Preferences from Request to Entity
        newUser.setNotifyForJobs(request.isNotifyForJobs());
        newUser.setNotifyForInternships(request.isNotifyForInternships());

        User savedUser = userRepository.save(newUser);

        // Convert to DTO safely
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public UserDTO updatePreferences(String email, boolean jobs, boolean internships) {
        User user = userRepository.findByEmail(email) // <--- CHANGED TO FIND BY EMAIL
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update logic remains the same
        if (jobs) user.setNotifyForJobs(true);
        if (internships) user.setNotifyForInternships(true);

        User savedUser = userRepository.save(user);

        // Convert to DTO manually
        UserDTO dto = new UserDTO();
        dto.setId(savedUser.getId());
        dto.setUsername(savedUser.getUsername());
        dto.setEmail(savedUser.getEmail());
        dto.setPhoneNumber(savedUser.getPhoneNumber());
        dto.setRole(savedUser.getRole().name());
        dto.setNotifyForJobs(savedUser.isNotifyForJobs());
        dto.setNotifyForInternships(savedUser.isNotifyForInternships());

        return dto;
    }
}