package com.example.sql_admin_auth.service;

import com.example.sql_admin_auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Find the user by EMAIL
        com.example.sql_admin_auth.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 2. 🛑 CRITICAL FIX: Return EMAIL as the username for Spring Security
        // This ensures the JWT Token 'subject' will be the email.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // <--- CHANGED from user.getUsername() to user.getEmail()
                user.getPassword(),
                Collections.singletonList(() -> "ROLE_" + user.getRole())
        );
    }
}