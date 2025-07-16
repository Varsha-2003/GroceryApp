package com.app.springapp.service;

import com.app.springapp.entity.User;
import com.app.springapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all users (admin only)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Get user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Create new user
    public User createUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role if not provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of("USER"));
        }
        
        return userRepository.save(user);
    }

    // Update user
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        
        // Only update password if provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        if (userDetails.getRoles() != null) {
            user.setRoles(userDetails.getRoles());
        }
        
        return userRepository.save(user);
    }

    // Delete user
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    // Check if user exists by username
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // Get users by role
    public List<User> getUsersByRole(String role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .toList();
    }

    // Update user roles
    public User updateUserRoles(Long id, Set<String> roles) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRoles(roles);
        return userRepository.save(user);
    }
} 