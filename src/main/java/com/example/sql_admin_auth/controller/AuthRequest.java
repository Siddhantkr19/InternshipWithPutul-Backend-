package com.example.sql_admin_auth.controller;

// A simple record to hold username and password from the login request
public record AuthRequest(String username, String password) {
}
