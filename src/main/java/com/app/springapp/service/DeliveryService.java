package com.app.springapp.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.springapp.entity.Delivery;
import com.app.springapp.entity.Product;
import com.app.springapp.repository.DeliveryRepository;
import com.app.springapp.repository.ProductRepository;


@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    public Delivery create(Delivery delivery) {
        return deliveryRepository.save(delivery);
    }

    public List<Delivery> getAll() {
        return deliveryRepository.findAll();
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
        return deliveryRepository.save(existing);
    }

    public void delete(Long id) {
        deliveryRepository.deleteById(id);
    }
}
