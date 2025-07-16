package com.app.springapp.repository;

import com.app.springapp.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCustomer_CustomerId(Long customerId);
    void deleteByCustomer_CustomerId(Long customerId);
} 