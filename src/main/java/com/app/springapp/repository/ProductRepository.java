package com.app.springapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.springapp.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 🔍 JPQL 1: Get products by category
    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> findByCategory(@Param("category") String category);

    // 🔍 Method 2: Get products by category (paginated) - using Spring Data naming convention
    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);

    // 🔍 JPQL 3: Search by name or brand (case insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    // Get only active products (paginated)
    Page<Product> findByActiveTrue(Pageable pageable);

    // Get only active products by category (paginated)
    Page<Product> findByCategoryIgnoreCaseAndActiveTrue(String category, Pageable pageable);
}
