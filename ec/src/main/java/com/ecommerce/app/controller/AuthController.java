package com.ecommerce.app.controller;

import com.ecommerce.app.dto.auth.LoginRequestDTO;
import com.ecommerce.app.dto.auth.UserRegistrationRequestDTO;
import com.ecommerce.app.dto.auth.JwtResponseDTO;
import com.ecommerce.app.dto.user.UserResponseDTO;
import com.ecommerce.app.dto.auth.ApiResponse; // Import ApiResponse
import com.ecommerce.app.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(@Valid @RequestBody UserRegistrationRequestDTO signUpRequest) {
        logger.info("Attempting to register user: {}", signUpRequest.getUsername());
        ApiResponse<UserResponseDTO> response = authService.registerUser(signUpRequest);
        logger.info("User {} registered successfully.", response.getData().getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED); 
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponseDTO>> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        logger.info("Attempting to authenticate user: {}", loginRequest.getUsernameOrEmail());
        ApiResponse<JwtResponseDTO> response = authService.authenticateUser(loginRequest);
        logger.info("User {} authenticated successfully.", response.getData().getUsername());
        return ResponseEntity.ok(response);
    }
}