package com.example.taskmanagement.security;

import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtTokenProvider {
    static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    // Updated SECRET_KEY with a secure 256-bit key (Base64-encoded for readability)
    private final String SECRET_KEY = "b2xkLXNlY3JldC1rZXktd2l0aC1hLXN1ZmZpY2llbnQtc2l6ZQ==";

    // Inject CustomUserDetailsService
    public JwtTokenProvider(CustomUserDetailsService customUserDetailsService,UserRepository userRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository =userRepository;
    }

    public String generateToken(User user) {
        long expirationTime = 86400000; // 1 day in milliseconds
        System.out.println("Generating token for user: " + user.getEmail());
        System.out.println("Expiration time in ms: " + expirationTime);

        // Since you no longer have roles, you don't need to get authorities
        List<String> roles = new ArrayList<>(); // Empty list of roles as there are none
        System.out.println("User roles: " + roles);

        Key key = getSigningKey();
        System.out.println("Signing key retrieved successfully.");

        // Create JWT token without adding roles
        String token = Jwts.builder()
                .setSubject(user.getEmail())  // Use email as the subject
                .setIssuedAt(new Date())  // Set issued date
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))  // Set expiration time
                .signWith(key, SignatureAlgorithm.HS256)  // Sign with HMAC and SHA-256
                .compact();

        System.out.println("Generated token: " + token);
        return token;
    }



    public boolean validateToken(String token) {
        try {
            Key key = getSigningKey();
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            System.out.println("Received Token: " + token); // Log the received token
            Key key = getSigningKey();
            System.out.println("Signing Key Retrieved Successfully."); // Log signing key retrieval
            String username = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            System.out.println("Extracted Username: " + username); // Log the extracted username
            return username;
        } catch (Exception e) {
            System.err.println("Error while parsing token: " + e.getMessage()); // Log errors
            throw e; // Re-throw the exception if needed
        }
    }

    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UsernamePasswordAuthenticationToken(user, null,Collections.emptyList() );
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
