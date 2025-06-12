package com.ecommerce.app.dto.order;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {
    @NotEmpty(message = "Order must contain at least one item.")
    @Valid
    private List<OrderItemRequestDTO> items;
}