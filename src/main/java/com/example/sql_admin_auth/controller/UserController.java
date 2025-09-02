package com.example.sql_admin_auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/data")
    public Map<String, String> getUserData() {
        return Map.of("message", "This is data for regular users.");
    }
}
