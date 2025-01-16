package com.example.taskmanagement.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class TaskRequest {

    @NotBlank(message = "Task name is required")
    @Size(max = 255, message = "Task name cannot exceed 255 characters")
    private String Title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Priority is required (e.g., HIGH, MEDIUM, LOW)")
    private String priority;

    @NotBlank(message = "Status is required (e.g., PENDING, IN_PROGRESS, COMPLETED)")
    private String status;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    // Getters and Setters
    public String getTitle() {
        return Title;
    }

    public void setTitle(String name) {
        this.Title = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}

