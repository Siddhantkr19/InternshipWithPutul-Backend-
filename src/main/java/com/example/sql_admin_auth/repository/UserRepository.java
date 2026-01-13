package com.example.sql_admin_auth.repository;

import com.example.sql_admin_auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Finds a user by their unique username
    Optional<User> findByUsername(String username);

    // Finds a user by their email (Required for Signup check)
    // NOTE: Ensure 'email' field exists in your User Entity!
    Optional<User> findByEmail(String email);

    // Helper to get all users sorted by newest first
    List<User> findAllByOrderByIdDesc();
}