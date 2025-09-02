package com.example.sql_admin_auth.repository;

import com.example.sql_admin_auth.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}