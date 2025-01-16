package com.example.taskmanagement.service;

import com.example.taskmanagement.DTOs.LoginRequest;
import com.example.taskmanagement.DTOs.LoginResponse;
import com.example.taskmanagement.DTOs.RegisterRequest;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Method to register a new user
    public ResponseEntity<String> registerUser(RegisterRequest registerRequest) {
        // Check if the user already exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already in use.");
        }

        // Hash the password
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Create a new user
        User user = new User();
        user.setUsername(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);

        // Save the user in the database
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully.");
    }

    // Method to authenticate and log in the user
    public ResponseEntity<?> loginUser(LoginRequest loginRequest) {
        try {
            // Find the user by email
            Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
            if (optionalUser.isEmpty()) {
                logger.info("No user found with email: {}", loginRequest.getEmail());
                throw new BadCredentialsException("Invalid email or password");
            }

            User user = optionalUser.get();
            logger.info("User found with email: {}", loginRequest.getEmail()); // Log only essential info

            // Verify the password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                logger.info("Password mismatch for user: {}", loginRequest.getEmail());
                throw new BadCredentialsException("Invalid email or password.");
            }

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user);

            // Optionally, set authentication context if using Spring Security
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Return the token in a structured response
            LoginResponse loginResponse = new LoginResponse("Login successful", token);
            return ResponseEntity.ok(loginResponse);

        } catch (BadCredentialsException e) {
            logger.error("Login failed for email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

}
