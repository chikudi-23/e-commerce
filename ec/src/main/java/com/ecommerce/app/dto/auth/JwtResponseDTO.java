package com.ecommerce.app.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponseDTO {
    private String token;
    private Long id;
    private String username;
    private String email;
    private String role;

    public JwtResponseDTO(String accessToken, Long id, String username, String email, String role) { // Constructor accepts String role
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role; // Assign the single role
    }
}