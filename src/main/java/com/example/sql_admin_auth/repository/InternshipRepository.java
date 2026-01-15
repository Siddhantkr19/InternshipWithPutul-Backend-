package com.example.sql_admin_auth.repository;

import com.example.sql_admin_auth.entity.Internship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {
    List<Internship> findAllByOrderByIdDesc();
    boolean existsByPostUrl(String postUrl);
}
