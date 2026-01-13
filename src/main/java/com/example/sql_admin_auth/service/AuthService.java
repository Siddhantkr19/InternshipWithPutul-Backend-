package com.example.sql_admin_auth.service;

import com.example.sql_admin_auth.controller.AuthRequest; // or create a DTO for this
import com.example.sql_admin_auth.dto.SignupRequest;
import com.example.sql_admin_auth.dto.UserDTO;
import com.example.sql_admin_auth.entity.PasswordResetToken;
import com.example.sql_admin_auth.entity.Role;
import com.example.sql_admin_auth.entity.User;
import com.example.sql_admin_auth.repository.PasswordResetTokenRepository;
import com.example.sql_admin_auth.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.sql_admin_auth.jwt.JwtService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    public AuthService(UserRepository userRepository,
                       PasswordResetTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       EmailService emailService,
                       ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
    }

    // 1. LOGIN LOGIC
    public Map<String, String> login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        final String jwt = jwtService.generateToken(userDetails);
        return Map.of("token", jwt);
    }

    // 2. SIGNUP LOGIC
    public UserDTO signup(SignupRequest request) {
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
        newUser.setRole(Role.USER); // Default to USER

        User savedUser = userRepository.save(newUser);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    // 3. FORGOT PASSWORD LOGIC
    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        emailService.sendEmail(
                user.getEmail(),
                "Password Reset Request",
                "Click here to reset your password: " + resetLink
        );
    }

    // 4. RESET PASSWORD LOGIC
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}