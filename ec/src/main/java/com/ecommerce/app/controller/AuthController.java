package com.ecommerce.app.controller;

import com.ecommerce.app.dto.auth.LoginRequestDTO;
import com.ecommerce.app.dto.auth.UserRegistrationRequestDTO;
import com.ecommerce.app.dto.auth.JwtResponseDTO;
import com.ecommerce.app.dto.user.UserResponseDTO;
import com.ecommerce.app.dto.auth.ApiResponse; // Import ApiResponse
import com.ecommerce.app.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // This method handles user login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponseDTO>> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        ApiResponse<JwtResponseDTO> response = authService.authenticateUser(loginRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // This method handles user registration
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(@Valid @RequestBody UserRegistrationRequestDTO signUpRequest) {
        ApiResponse<UserResponseDTO> response = authService.registerUser(signUpRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}