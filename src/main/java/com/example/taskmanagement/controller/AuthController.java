package com.example.taskmanagement.controller;

import com.example.taskmanagement.DTOs.LoginRequest;
import com.example.taskmanagement.DTOs.RegisterRequest;
import com.example.taskmanagement.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequestMapping("/api/auth")
public class AuthController {



    private final AuthService authService;

    // Injecting AuthService into the controller
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/test-login")
    public ResponseEntity<?> testLogin(@RequestBody LoginRequest loginRequest) {
        System.out.println("LoginRequest as JSON: " + loginRequest);
        System.out.println("Received email: " + loginRequest.getEmail());
        System.out.println("Received password: " + loginRequest.getPassword());
        return ResponseEntity.ok("Test successful");
    }

    // Register user endpoint, uses AuthService to handle registration logic
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        // Directly use the AuthService method for registration and return the response
        System.out.println("Register request received: " + registerRequest);
        return authService.registerUser(registerRequest);  // Calling AuthService method
    }

    // Login user endpoint, uses AuthService to handle login logic
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login request received: " + loginRequest);
        try {
            // Call AuthService to log in and get a token
            return authService.loginUser(loginRequest);  // Calling AuthService method
        } catch (RuntimeException e) {
            // Catch any runtime exceptions and return unauthorized
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
}
