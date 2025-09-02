package com.example.sql_admin_auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin") // All routes in this controller will start with /admin
public class AdminController {

    @GetMapping("/data")
    public Map<String, String> getAdminData() {
        // This data can only be accessed by users with the 'ADMIN' role
        return Map.of("message", "This is secret data from the admin endpoint!");
    }
}
