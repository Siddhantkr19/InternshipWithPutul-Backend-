package com.example.sql_admin_auth.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // Good practice, since "user" can be a reserved keyword
@Data // Provides getters, setters, toString, etc.
@NoArgsConstructor // Required by JPA for creating new instances
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // Ensures usernames are unique
    private String username;

    private String password;

    private String role; // To store the user's role, like "ADMIN"
}