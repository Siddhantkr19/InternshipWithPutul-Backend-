package com.example.sql_admin_auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Internship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String role;
    @Column(length = 1000)
    private String eligibility;
    private String stipend;
    private String location;
    @Column(length = 1000)
    private String applyLink;
    private String duration;
    private String lastDateToApply;
    @Column(unique = true)
    private String postUrl;
}
