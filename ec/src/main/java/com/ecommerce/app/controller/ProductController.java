package com.ecommerce.app.controller;

import com.ecommerce.app.dto.auth.ApiResponse;
import com.ecommerce.app.dto.product.ProductRequestDTO;
import com.ecommerce.app.dto.product.ProductResponseDTO;
import com.ecommerce.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // Make sure this import is present. If using Spring Boot 3+, it might be jakarta.validation.Valid
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Create Product (Admin Only)
    @PostMapping("/admin/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(@Valid @RequestBody ProductRequestDTO productRequest) {
        ProductResponseDTO createdProduct = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Product created successfully.", HttpStatus.CREATED.value(), createdProduct));
    }

    // Get All Products (Accessible by any authenticated user)
    @GetMapping("/products")
    @PreAuthorize("isAuthenticated()") // Or remove if truly public
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(new ApiResponse<>("Products retrieved successfully.", HttpStatus.OK.value(), products));
    }

    // Get Product by ID (Accessible by any authenticated user)
    @GetMapping("/products/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(new ApiResponse<>("Product retrieved successfully.", HttpStatus.OK.value(), product));
    }

    // Update Product (Admin Only)
    @PutMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productRequest) {
        ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(new ApiResponse<>("Product updated successfully.", HttpStatus.OK.value(), updatedProduct));
    }

    // Delete Product (Admin Only)
    @DeleteMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse<>("Product deleted successfully.", HttpStatus.OK.value(), null));
    }
}