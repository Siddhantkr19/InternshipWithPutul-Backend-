package com.example.sql_admin_auth.controller;

import com.example.sql_admin_auth.dto.ForgotPasswordRequest;
import com.example.sql_admin_auth.dto.ResetPasswordRequest;
import com.example.sql_admin_auth.dto.SignupRequest;
import com.example.sql_admin_auth.dto.UserDTO;
import com.example.sql_admin_auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // Constructor Injection
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.processForgotPassword(request.getEmail());
        return ResponseEntity.ok("Reset link sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password has been reset successfully.");
    }
}