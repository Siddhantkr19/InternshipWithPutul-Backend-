package com.example.sql_admin_auth.controller;

import com.example.sql_admin_auth.entity.Visitor;
import com.example.sql_admin_auth.service.VisitorService; // Import the service
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class VisitorController {

    private final VisitorService visitorService;

    // Use constructor injection - this is a best practice
    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @GetMapping("/api/visits")
    public ResponseEntity<Visitor> getVisitorCount() {
        Visitor visitor = visitorService.getVisitorCount();
        return ResponseEntity.ok(visitor);
    }

    @PostMapping("/api/visits")
    public ResponseEntity<Visitor> incrementVisitorCount() {
        Visitor visitor = visitorService.incrementAndGetCount();
        return ResponseEntity.ok(visitor);
    }
}