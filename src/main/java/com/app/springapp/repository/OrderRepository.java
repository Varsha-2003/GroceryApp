package com.app.springapp.repository;

import org.springframework.stereotype.Repository;

import com.app.springapp.entity.Order;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 🔍 JPQL 1: Get orders by customer ID
    @Query("SELECT o FROM Order o WHERE o.customer.customerId = :customerId")
    List<Order> findOrdersByCustomerId(@Param("customerId") Long customerId);

    // 🔍 JPQL 2: Get all orders placed within a date range
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findOrdersWithinDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // 🔍 Method 3: Get orders by status (paginated) - using Spring Data naming convention
    Page<Order> findByStatusIgnoreCase(String status, Pageable pageable);
}
