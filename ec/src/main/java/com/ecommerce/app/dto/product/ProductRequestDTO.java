package com.ecommerce.app.dto.product;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDTO {
    @NotBlank(message = "Product name cannot be empty.")
    @Size(max = 255, message = "Product name must be less than 255 characters.")
    private String name;

    @NotBlank(message = "Product description cannot be empty.")
    @Size(max = 1000, message = "Product description must be less than 1000 characters.")
    private String description;

    @NotNull(message = "Product price cannot be null.")
    @DecimalMin(value = "0.01", message = "Price must be greater than or equal to 0.01.")
    private BigDecimal price;

    @NotBlank(message = "Product category cannot be empty.")
    @Size(max = 100, message = "Category must be less than 100 characters.")
    private String category;

    @NotNull(message = "Stock quantity cannot be null.")
    @Min(value = 0, message = "Stock quantity cannot be negative.")
    private Integer stockQuantity;

}