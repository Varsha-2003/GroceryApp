package com.app.springapp.service;

import com.app.springapp.entity.Order;
import com.app.springapp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    /* ---------- CRUD ---------- */

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    /** Paginated & sorted list */
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public Order updateOrder(Long id, Order updatedOrder) {
        Order existing = getOrderById(id);
        existing.setOrderDate(updatedOrder.getOrderDate());
        existing.setTotalAmount(updatedOrder.getTotalAmount());
        existing.setPaymentMethod(updatedOrder.getPaymentMethod());
        existing.setStatus(updatedOrder.getStatus());
        existing.setCustomer(updatedOrder.getCustomer());
        return orderRepository.save(existing);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
