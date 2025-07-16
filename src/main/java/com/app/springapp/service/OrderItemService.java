package com.app.springapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.springapp.entity.Order;
import com.app.springapp.entity.OrderItem;
import com.app.springapp.entity.Product;
import com.app.springapp.repository.OrderItemRepository;
import com.app.springapp.repository.OrderRepository;
import com.app.springapp.repository.CustomerRepository;
import com.app.springapp.repository.ProductRepository;
import com.app.springapp.entity.Customer;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

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

    public List<OrderItem> getCartItemsByCustomer(Long customerId) {
        List<OrderItem> items = orderItemRepository.findByCustomer_CustomerIdAndOrderIsNull(customerId);
        System.out.println("Cart items for customer " + customerId + ": " + items.size());
        return items;
    }

    public List<OrderItem> getCartItems(Long customerId) {
        return orderItemRepository.findByCustomer_CustomerIdAndOrderIsNull(customerId);
    }

    public OrderItem addCartItem(Long customerId, Long productId, int quantity) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        List<OrderItem> cartItems = orderItemRepository.findByCustomer_CustomerIdAndOrderIsNull(customerId);
        OrderItem existing = cartItems.stream()
            .filter(item -> item.getProduct().getProductId().equals(productId))
            .findFirst().orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            return orderItemRepository.save(existing);
        } else {
            OrderItem newItem = new OrderItem();
            newItem.setCustomer(customer);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setOrder(null);
            newItem.setPrice(product.getPrice());
            return orderItemRepository.save(newItem);
        }
    }
}

