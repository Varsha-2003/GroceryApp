package com.app.springapp.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;

    private String deliveryPartnerName;
    private String status; // e.g., "Assigned", "Picked Up", "Delivered"
    private Date estimatedDeliveryTime;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
