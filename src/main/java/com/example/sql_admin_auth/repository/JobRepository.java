package com.example.sql_admin_auth.repository;

import com.example.sql_admin_auth.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    // Custom method to fetch all jobs, sorted by ID (Newest first)
    List<Job> findAllByOrderByIdDesc();
    boolean existsByPostUrl(String postUrl);
}
