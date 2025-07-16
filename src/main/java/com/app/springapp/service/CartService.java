package com.app.springapp.service;

import com.app.springapp.entity.CartItem;
import com.app.springapp.entity.Customer;
import com.app.springapp.entity.Product;
import com.app.springapp.repository.CartItemRepository;
import com.app.springapp.repository.CustomerRepository;
import com.app.springapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;

    public CartItem addToCart(Long customerId, Long productId, int quantity) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        CartItem cartItem = cartItemRepository.findByCustomer_CustomerId(customerId).stream()
            .filter(item -> item.getProduct().getProductId().equals(productId))
            .findFirst().orElse(null);
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        return cartItemRepository.save(cartItem);
    }

    public List<CartItem> getCartItems(Long customerId) {
        return cartItemRepository.findByCustomer_CustomerId(customerId);
    }

    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public void clearCart(Long customerId) {
        cartItemRepository.deleteByCustomer_CustomerId(customerId);
    }
} 