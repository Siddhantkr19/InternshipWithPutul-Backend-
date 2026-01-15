package com.example.sql_admin_auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User implements UserDetails {

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
    // --- NEW NOTIFICATION PREFERENCES ---
    @Column(columnDefinition = "boolean default false")
    private boolean notifyForJobs;

    @Column(columnDefinition = "boolean default false")
    private boolean notifyForInternships;

    // --- UserDetails Methods ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}