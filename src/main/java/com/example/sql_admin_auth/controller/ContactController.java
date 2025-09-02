package com.example.sql_admin_auth.controller;

import com.example.sql_admin_auth.entity.ContactMessage;
import com.example.sql_admin_auth.repository.ContactMessageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactMessageRepository messageRepository;

    public ContactController(ContactMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @PostMapping
    public ResponseEntity<String> submitContactForm(@RequestBody ContactMessage message) {
        messageRepository.save(message);
        return ResponseEntity.ok("Message received successfully!");
    }
}