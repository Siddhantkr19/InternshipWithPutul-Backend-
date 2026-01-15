package com.example.sql_admin_auth.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;    // User's login ID
    private String email;       // Required for uniqueness check
    private String password;
    private String phoneNumber; // New field
    private String course;
    private boolean notifyForJobs;
    private boolean notifyForInternships;// New field (e.g., BCA, B.Tech)
}