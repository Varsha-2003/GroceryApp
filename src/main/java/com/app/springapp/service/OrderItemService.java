package com.app.springapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.springapp.entity.Order;
import com.app.springapp.entity.OrderItem;
import com.app.springapp.entity.Product;
import com.app.springapp.repository.OrderItemRepository;
import com.app.springapp.repository.OrderRepository;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    public OrderItem create(OrderItem item) {
        return orderItemRepository.save(item);
    }

    public List<OrderItem> getAll() {
        return orderItemRepository.findAll();
    }

    public OrderItem getById(Long id) {
        return orderItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("OrderItem not found"));
    }

    public OrderItem update(Long id, OrderItem item) {
        OrderItem existing = getById(id);
        existing.setQuantity(item.getQuantity());
        existing.setPrice(item.getPrice());
        existing.setProduct(item.getProduct());
        existing.setOrder(item.getOrder());
        return orderItemRepository.save(existing);
    }

    public void delete(Long id) {
        orderItemRepository.deleteById(id);
    }
}

