package com.example.sql_admin_auth.controller;

import com.example.sql_admin_auth.entity.ContactMessage;
import com.example.sql_admin_auth.entity.User;
import com.example.sql_admin_auth.repository.ContactMessageRepository;
import com.example.sql_admin_auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/management")
public class ManagementController {

    private final UserService userService;
    private final ContactMessageRepository messageRepository;

    public ManagementController(UserService userService, ContactMessageRepository messageRepository) {
        this.userService = userService;
        this.messageRepository = messageRepository;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/messages")
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        // Fetch messages in descending order of creation
        List<ContactMessage> messages = messageRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt")
        );
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}