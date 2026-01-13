package com.example.sql_admin_auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Unique Login ID

    @Column(unique = true, nullable = false)
    private String email;    // Unique Email

    private String phoneNumber;

    private String course;   // e.g., BCA, B.Tech

    private String password;

    // --- CHANGED FROM STRING TO ENUM ---
    @Enumerated(EnumType.STRING) // Stores "ADMIN" or "USER" as text in DB
    private Role role;
}