package com.app.springapp.controller;

import com.app.springapp.entity.CartItem;
import com.app.springapp.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@RequestParam Long customerId, @RequestParam Long productId, @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.addToCart(customerId, productId, quantity));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.getCartItems(customerId));
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear/{customerId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }
} 