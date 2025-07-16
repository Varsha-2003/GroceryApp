package com.app.springapp.controller;

import com.app.springapp.entity.Order;
import com.app.springapp.service.OrderService;
import com.app.springapp.dto.OrderAdminDto;
import com.app.springapp.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /* ---------- Create ---------- */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return new ResponseEntity<>(orderService.createOrder(order), HttpStatus.CREATED);
    }

    /* ---------- Read (paginated & sorted) ---------- */
    @GetMapping
    public ResponseEntity<Page<Order>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "orderId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        
        if (status != null && !status.isEmpty()) {
            return ResponseEntity.ok(orderService.getOrdersByStatus(status, pageable));
        } else {
            return ResponseEntity.ok(orderService.getAllOrders(pageable));
        }
    }

    /* ---------- Read (single) ---------- */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /* ---------- Update ---------- */
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id,
            @RequestBody Order order) {
        return ResponseEntity.ok(orderService.updateOrder(id, order));
    }

    /* ---------- Delete ---------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    /* ---------- Admin: Read (paginated & sorted, with customer & delivery info) ---------- */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Page<OrderAdminDto>> getAllOrdersAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "orderId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(orderService.getAllOrdersAdmin(pageable));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(orderService.count());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDto>> getOrdersByCustomer(@PathVariable Long customerId) {
        try {
            List<OrderDto> orders = orderService.getOrderDtosByCustomerId(customerId);
            return ResponseEntity.ok(orders != null ? orders : Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }
}
