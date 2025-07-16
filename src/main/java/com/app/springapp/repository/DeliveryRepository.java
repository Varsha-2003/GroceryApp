package com.app.springapp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.springapp.entity.Delivery;
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByOrder_Customer_CustomerId(Long customerId);
    Delivery findByOrder_OrderId(Long orderId);
}
