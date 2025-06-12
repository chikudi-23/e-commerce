package com.ecommerce.app.dto.user;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Getter
@Setter
public class UserUpdateRequestDTO {
    @NotBlank(message = "First name cannot be empty.")
    @Size(max = 50, message = "First name must be less than 50 characters.")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty.")
    @Size(max = 50, message = "Last name must be less than 50 characters.")
    private String lastName;

    @NotBlank(message = "Phone number cannot be empty.")
    @Size(max = 15, message = "Phone number must be less than 15 characters.")
    private String phoneNumber;

    @NotBlank(message = "Apartment name cannot be empty.")
    @Size(max = 100, message = "Apartment name must be less than 100 characters.")
    private String apartmentName;

    @NotBlank(message = "Street address cannot be empty.")
    @Size(max = 100, message = "Street address must be less than 100 characters.")
    private String streetAddress;

    @NotBlank(message = "City cannot be empty.")
    @Size(max = 50, message = "City must be less than 50 characters.")
    private String city;

    @NotBlank(message = "Country cannot be empty.")
    @Size(max = 50, message = "Country must be less than 50 characters.")
    private String country;

    @NotBlank(message = "Pincode cannot be empty.")
    @Size(max = 10, message = "Pincode must be less than 10 characters.")
    private String pincode;

    @NotBlank(message = "Email cannot be empty.")
    @Size(max = 50, message = "Email must be less than 50 characters.")
    @Email(message = "Email must be valid.")
    private String email;
}