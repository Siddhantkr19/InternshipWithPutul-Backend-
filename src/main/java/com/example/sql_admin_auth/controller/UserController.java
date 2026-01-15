package com.example.sql_admin_auth.controller;

import com.example.sql_admin_auth.dto.UserDTO;
import com.example.sql_admin_auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
   @Autowired
   private UserService userService;
    @GetMapping("/data")
    public Map<String, String> getUserData() {
        return Map.of("message", "This is data for regular users.");
    }

    @PutMapping("/preferences")
    public ResponseEntity<UserDTO> updateNotificationPreferences(
            @RequestBody Map<String, Boolean> preferences,
            java.security.Principal principal) {

        String email = principal.getName(); // Get logged-in user's email

        boolean jobs = preferences.getOrDefault("notifyForJobs", false);
        boolean internships = preferences.getOrDefault("notifyForInternships", false);

        UserDTO updatedUser = userService.updatePreferences(email, jobs, internships);
        return ResponseEntity.ok(updatedUser);
    }
}
