package com.ecommerce.app.repository;

import com.ecommerce.app.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory(String category, Pageable pageable);

    boolean existsByName(String name);
    // Optional: if you need to retrieve the product by name
    Optional<Product> findByName(String name);
}