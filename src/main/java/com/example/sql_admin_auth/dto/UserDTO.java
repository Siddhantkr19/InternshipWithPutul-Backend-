package com.example.sql_admin_auth.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String course;
    private String role;
}