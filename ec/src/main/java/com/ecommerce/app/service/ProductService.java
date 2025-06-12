package com.ecommerce.app.service;

import com.ecommerce.app.dto.product.ProductRequestDTO;
import com.ecommerce.app.dto.product.ProductResponseDTO;
import com.ecommerce.app.exception.ResourceNotFoundException; // For category not found
import com.ecommerce.app.model.Product;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.CategoryRepository; // Import CategoryRepository
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository; // Inject CategoryRepository to check existence

    @Transactional
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {

        if (productRepository.existsByName(productRequestDTO.getName())) {
            throw new IllegalArgumentException("Product with name '" + productRequestDTO.getName() + "' already exists.");
        }

        if (!categoryRepository.existsByName(productRequestDTO.getCategory())) {
            throw new ResourceNotFoundException("Category with name '" + productRequestDTO.getCategory() + "' does not exist. Please create it first.");
        }

        Product product = new Product();
        BeanUtils.copyProperties(productRequestDTO, product);
        Product savedProduct = productRepository.save(product);
        return convertToProductDTO(savedProduct);
    }

    @Cacheable("products")
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }


    @Cacheable(value = "productById", key = "#id")
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToProductDTO(product);
    }

    @Transactional
    @CachePut(value = "productById", key = "#id")
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));


        if (!existingProduct.getName().equals(productRequestDTO.getName()) && productRepository.existsByName(productRequestDTO.getName())) {
            throw new IllegalArgumentException("Product with name '" + productRequestDTO.getName() + "' already exists for another product.");
        }

        if (!categoryRepository.existsByName(productRequestDTO.getCategory())) {
            throw new ResourceNotFoundException("Category with name '" + productRequestDTO.getCategory() + "' does not exist. Please create it first.");
        }

        BeanUtils.copyProperties(productRequestDTO, existingProduct, "id"); // Don't copy ID from DTO
        Product updatedProduct = productRepository.save(existingProduct);
        return convertToProductDTO(updatedProduct);
    }

    @Transactional
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponseDTO convertToProductDTO(Product product) {
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        BeanUtils.copyProperties(product, productResponseDTO);
        return productResponseDTO;
    }
}