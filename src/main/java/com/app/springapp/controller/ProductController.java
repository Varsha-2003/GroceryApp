package com.app.springapp.controller;

import com.app.springapp.entity.Product;
import com.app.springapp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /* ---------- Create ---------- */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return new ResponseEntity<>(productService.createProduct(product), HttpStatus.CREATED);
    }

    /* ---------- Read (paginated & sorted, only active for users) ---------- */
    @GetMapping
    public ResponseEntity<Page<Product>> getAllActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String category) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(productService.getActiveProductsByCategory(category, pageable));
        } else {
            return ResponseEntity.ok(productService.getAllActiveProducts(pageable));
        }
    }

    /* ---------- Read (single) ---------- */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /* ---------- Update ---------- */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
            @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    /* ---------- Toggle product visibility (admin only) ---------- */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/active")
    public ResponseEntity<Product> setProductActiveStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(productService.setProductActiveStatus(id, active));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<Product>> getAllProductsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String category) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(productService.getProductsByCategory(category, pageable));
        } else {
            return ResponseEntity.ok(productService.getAllProducts(pageable));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(productService.count());
    }

    // Simple test DELETE endpoint for debugging
    @DeleteMapping("/test-delete")
    public ResponseEntity<?> testDelete() {
        System.out.println("Test DELETE endpoint hit!");
        return ResponseEntity.ok("Deleted!");
    }

    // Simple test GET endpoint for debugging
    @GetMapping("/test-get")
    public ResponseEntity<?> testGet() {
        System.out.println("Test GET endpoint hit!");
        return ResponseEntity.ok("Got it!");
    }

    // Debug endpoint to check current user's authorities
    @GetMapping("/test/roles")
    public ResponseEntity<?> testRoles(Authentication authentication) {
        return ResponseEntity.ok(authentication.getAuthorities());
    }
}
