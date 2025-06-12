package com.ecommerce.app.controller;

import com.ecommerce.app.dto.auth.ApiResponse;
import com.ecommerce.app.dto.order.OrderRequestDTO;
import com.ecommerce.app.dto.order.OrderResponseDTO;
import com.ecommerce.app.dto.order.OrderStatusUpdateRequestDTO;
import com.ecommerce.app.service.OrderService;
import com.ecommerce.app.security.UserDetailsImpl;
import com.ecommerce.app.exception.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Helper to get authenticated user's ID
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) authentication.getPrincipal()).getId();
        }
        throw new IllegalStateException("Authenticated user ID not found."); // Should not happen if authenticated
    }

    // Helper to check if authenticated user is ADMIN
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // Place New Order (Any authenticated user)
    @PostMapping("/orders")
    @PreAuthorize("isAuthenticated()") // User must be logged in
    public ResponseEntity<ApiResponse<OrderResponseDTO>> placeNewOrder(
            @Valid @RequestBody OrderRequestDTO orderRequest) {

        Long authenticatedUserId = getAuthenticatedUserId();
        OrderResponseDTO newOrder = orderService.placeNewOrder(authenticatedUserId, orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Order placed successfully.", HttpStatus.CREATED.value(), newOrder));
    }

    // Get Order Details by ID (Authenticated user; must be their order or admin)
    @GetMapping("/orders/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderDetails(@PathVariable Long id) {
        OrderResponseDTO orderDetails = orderService.getOrderDetails(id); // Service will throw if not found

        Long authenticatedUserId = getAuthenticatedUserId();
        // Check if the order belongs to the authenticated user or if the user is an ADMIN
        if (!orderDetails.getUserId().equals(authenticatedUserId) && !isAdmin()) {
            throw new AccessDeniedException("You are not authorized to view this order.");
        }

        return ResponseEntity.ok(new ApiResponse<>("Order retrieved successfully.", HttpStatus.OK.value(), orderDetails));
    }

    // Get User's Order History (Admin can view any user's orders, user can view their own)
    @GetMapping("/users/{userId}/orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<OrderResponseDTO>>> getUserOrderHistory(
            @PathVariable Long userId,
            Pageable pageable) {

        Long authenticatedUserId = getAuthenticatedUserId();
        // Ensure user can only view their own order history, unless they are ADMIN
        if (!userId.equals(authenticatedUserId) && !isAdmin()) {
            throw new AccessDeniedException("You are not authorized to view this user's order history.");
        }

        Page<OrderResponseDTO> ordersPage = orderService.getUserOrderHistory(userId, pageable);
        return ResponseEntity.ok(new ApiResponse<>("Order history retrieved successfully.", HttpStatus.OK.value(), ordersPage));
    }

    // Update Order Status (Admin Only)
    @PutMapping("/admin/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequestDTO statusUpdateRequest) {
        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(id, statusUpdateRequest.getStatus());
        return ResponseEntity.ok(new ApiResponse<>("Order status updated successfully.", HttpStatus.OK.value(), updatedOrder));
    }
}