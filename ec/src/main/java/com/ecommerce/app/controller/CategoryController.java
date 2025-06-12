package com.ecommerce.app.controller;

import com.ecommerce.app.dto.auth.ApiResponse;
import com.ecommerce.app.dto.category.CategoryRequestDTO;
import com.ecommerce.app.dto.category.CategoryResponseDTO;
import com.ecommerce.app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(@Valid @RequestBody CategoryRequestDTO categoryRequest) {
        CategoryResponseDTO createdCategory = categoryService.createCategory(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Category created successfully.", HttpStatus.CREATED.value(), createdCategory));
    }

    @GetMapping("/categories")
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>("Categories retrieved successfully.", HttpStatus.OK.value(), categories));
    }
}