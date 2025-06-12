package com.ecommerce.app.dto.order;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class OrderItemRequestDTO {
    @NotNull(message = "Product ID cannot be null.")
    private Long productId;

    @NotNull(message = "Quantity cannot be null.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity;
}