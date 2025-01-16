package com.example.taskmanagement.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

public class LoginRequest {

    @NotEmpty(message = " Email is required")
    @JsonProperty("Email")
    private String Email;

    @NotEmpty(message = "Password is required")
    @JsonProperty("password")
    private String password;

    // Getters and Setters
    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
