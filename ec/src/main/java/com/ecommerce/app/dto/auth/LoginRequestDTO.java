package com.ecommerce.app.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LoginRequestDTO {

    private String username;
    private String password;

    public LoginRequestDTO() {
    }


    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}