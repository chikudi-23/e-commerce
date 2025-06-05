package com.ecommerce.app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Generic API response wrapper
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> { // <T> means it can hold any type of data
    private String message;
    private int status; // HTTP Status code (e.g., 200, 201)
    private T data;     // The actual response payload (could be JwtResponseDTO, UserResponseDTO, etc.)

    // Constructor without data (e.g., for success messages without a specific payload)
    public ApiResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.data = null;
    }
}