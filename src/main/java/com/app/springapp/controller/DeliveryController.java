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
import org.springframework.web.bind.annotation.RestController;

import com.app.springapp.entity.Delivery;
import com.app.springapp.service.DeliveryService;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<Delivery> create(@RequestBody Delivery delivery) {
        return new ResponseEntity<>(deliveryService.create(delivery), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Delivery>> getAll() {
        return ResponseEntity.ok(deliveryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Delivery> getById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Delivery> update(@PathVariable Long id, @RequestBody Delivery delivery) {
        return ResponseEntity.ok(deliveryService.update(id, delivery));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deliveryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

