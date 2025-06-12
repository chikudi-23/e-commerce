package com.ecommerce.app.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String apartmentName;
    private String streetAddress;
    private String city;
    private String country;
    private String state;
    private String pincode;

    public UserResponseDTO() {}
}