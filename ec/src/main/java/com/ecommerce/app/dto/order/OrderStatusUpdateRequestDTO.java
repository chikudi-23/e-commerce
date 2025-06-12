package com.ecommerce.app.dto.order;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
public class OrderStatusUpdateRequestDTO {
    @NotBlank(message = "Status cannot be empty.")
    @Pattern(regexp = "PENDING|PROCESSING|SHIPPED|DELIVERED|CANCELLED", message = "Invalid order status. Must be one of PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED.")
    private String status;
}