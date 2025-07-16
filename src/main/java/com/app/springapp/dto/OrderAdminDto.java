package com.app.springapp.dto;

import lombok.Data;

@Data
public class OrderAdminDto {
    private Long orderId;
    private String orderDate;
    private double totalAmount;
    private String paymentMethod;
    private String status;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String deliveryDate;
    private Double deliveryCost;
} 