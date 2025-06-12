package com.ecommerce.app.dto.category;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class CategoryRequestDTO {
    @NotBlank(message = "Category name cannot be empty.")
    @Size(max = 100, message = "Category name must be less than 100 characters.")
    private String name;
}