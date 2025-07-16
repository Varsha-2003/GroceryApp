package com.app.springapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.springapp.entity.OrderItem;
import com.app.springapp.service.OrderItemService;
import java.security.Principal;
import com.app.springapp.entity.Customer;
import com.app.springapp.repository.CustomerRepository;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private CustomerRepository customerRepo;

    @PostMapping
    public ResponseEntity<OrderItem> create(@RequestBody OrderItem item) {
        return new ResponseEntity<>(orderItemService.create(item), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderItem>> getAll() {
        return ResponseEntity.ok(orderItemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> update(@PathVariable Long id, @RequestBody OrderItem item) {
        return ResponseEntity.ok(orderItemService.update(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cart/{customerId}")
    public ResponseEntity<List<OrderItem>> getCartItems(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderItemService.getCartItems(customerId));
    }

    @PostMapping("/cart/add")
    public ResponseEntity<OrderItem> addCartItem(@RequestParam Long customerId, @RequestParam Long productId, @RequestParam int quantity) {
        return ResponseEntity.ok(orderItemService.addCartItem(customerId, productId, quantity));
    }
}
