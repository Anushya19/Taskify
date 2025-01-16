package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(@Param("email") String email);

    // Optionally, if you want a method for checking if a username exists
    Boolean existsByUsername(String username);

    // Optionally, if you want a method for checking if an email exists
    Boolean existsByEmail(String email);
}
