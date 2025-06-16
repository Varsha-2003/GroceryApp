package com.app.springapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.springapp.entity.Delivery;
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
