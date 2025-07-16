package com.app.springapp.service;

import com.app.springapp.entity.Product;
import com.app.springapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /* ---------- CRUD ---------- */

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /** Paginated & sorted list */
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /** Paginated & sorted list by category */
    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryIgnoreCase(category, pageable);
    }

    /** Paginated & sorted list of only active products (for users) */
    public Page<Product> getAllActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    /** Paginated & sorted list of only active products by category (for users) */
    public Page<Product> getActiveProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryIgnoreCaseAndActiveTrue(category, pageable);
    }

    /** Toggle product visibility (active status) */
    public Product setProductActiveStatus(Long id, boolean active) {
        Product product = getProductById(id);
        product.setActive(active);
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);
        existing.setProductName(updatedProduct.getProductName());
        existing.setCategory(updatedProduct.getCategory());
        existing.setBrand(updatedProduct.getBrand());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStockQuantity(updatedProduct.getStockQuantity());
        existing.setImageUrl(updatedProduct.getImageUrl());
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }

    public long count() {
        return productRepository.count();
    }
}
