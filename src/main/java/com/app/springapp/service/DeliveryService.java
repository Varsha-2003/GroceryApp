package com.app.springapp.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.springapp.entity.Delivery;
import com.app.springapp.entity.Product;
import com.app.springapp.repository.DeliveryRepository;
import com.app.springapp.repository.ProductRepository;
import com.app.springapp.repository.OrderRepository;
import com.app.springapp.entity.Order;


@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Delivery create(Delivery delivery) {
        if (delivery.getOrder() != null && delivery.getOrder().getOrderId() != null) {
            Long orderId = delivery.getOrder().getOrderId();
            delivery.setOrder(orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found for delivery")));
        }
        return deliveryRepository.save(delivery);
    }

    public List<Delivery> getAll() {
        return deliveryRepository.findAll();
    }

    public List<Delivery> getByCustomerId(Long customerId) {
        return deliveryRepository.findByOrder_Customer_CustomerId(customerId);
    }

    public Delivery getById(Long id) {
        return deliveryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Delivery not found"));
    }

    public Delivery update(Long id, Delivery delivery) {
        Delivery existing = getById(id);
        existing.setDeliveryPartnerName(delivery.getDeliveryPartnerName());
        existing.setStatus(delivery.getStatus());
        existing.setEstimatedDeliveryTime(delivery.getEstimatedDeliveryTime());
        existing.setOrder(delivery.getOrder());
        existing.setDeliveryAddress(delivery.getDeliveryAddress());
        existing.setTotalCost(delivery.getTotalCost());
        return deliveryRepository.save(existing);
    }

    public void delete(Long id) {
        deliveryRepository.deleteById(id);
    }

    public Delivery getByOrderId(Long orderId) {
        return deliveryRepository.findByOrder_OrderId(orderId);
    }
}
