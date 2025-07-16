package com.app.springapp.controller;

import com.app.springapp.dto.SignupRequest;
import com.app.springapp.dto.LoginRequest;
import com.app.springapp.entity.Customer;
import com.app.springapp.repository.CustomerRepository;
import com.app.springapp.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.app.springapp.entity.User;
import com.app.springapp.repository.UserRepository;
import java.util.Map;
import org.springframework.http.HttpStatus;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    /* ---------- TEST ENDPOINT ---------- */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("message", "Backend is running!", "status", "success"));
    }

    /* ---------- ECHO ENDPOINT FOR TESTING ---------- */
    @PostMapping("/echo")
    public ResponseEntity<?> echo(@RequestBody Object request) {
        System.out.println("Echo endpoint called with: " + request);
        return ResponseEntity.ok(Map.of("echo", request, "message", "Request received"));
    }

    /* ---------- SIGN‑UP ---------- */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        System.out.println("Signup request received: " + req.getEmail());
        if (customerRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
        }
        Customer c = new Customer();
        c.setFullName(req.getCustomername());
        c.setEmail(req.getEmail());
        c.setPassword(encoder.encode(req.getPassword()));
        c.setAddress(req.getAdress());
        c.setPhone(req.getPhonenumber() != null ? req.getPhonenumber().toString() : null);
        Customer savedCustomer = customerRepo.save(c);
        System.out.println("Customer saved with ID: " + savedCustomer.getCustomerId());
        return ResponseEntity.ok(Map.of("message", "Signup successful"));
    }

    /* ---------- LOG‑IN ---------- */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        System.out.println("Login attempt for email: " + req.getEmail());
        Customer c = customerRepo.findByEmail(req.getEmail()).orElse(null);
        if (c == null) {
            System.out.println("Customer not found for email: " + req.getEmail());
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
        if (!encoder.matches(req.getPassword(), c.getPassword())) {
            System.out.println("Password mismatch for email: " + req.getEmail());
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
        String token = jwtUtil.generateToken(c.getEmail(), Set.of("USER"));
        System.out.println("Login successful for email: " + req.getEmail());
        return ResponseEntity.ok(Map.of(
            "token", token,
            "customerId", c.getCustomerId(),
            "message", "Login successful"
        ));
    }

    /* ---------- ADMIN LOG‑IN ---------- */
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest req) {
        System.out.println("Admin login attempt for username: " + req.getEmail());
        User user = userRepository.findByUsername(req.getEmail()).orElse(null);
        if (user == null) {
            System.out.println("Admin user not found for username: " + req.getEmail());
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            System.out.println("Password mismatch for admin username: " + req.getEmail());
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
        // Check if user has ADMIN role
        if (!user.getRoles().contains("ADMIN")) {
            System.out.println("User does not have ADMIN role: " + req.getEmail());
            return ResponseEntity.status(403).body(Map.of("message", "Access denied. Admin privileges required."));
        }
        // Map roles to ROLE_ prefix for Spring Security
        Set<String> springRoles = user.getRoles().stream()
            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
            .collect(Collectors.toSet());
        String token = jwtUtil.generateToken(user.getUsername(), springRoles);
        System.out.println("Admin login successful for username: " + req.getEmail());
        return ResponseEntity.ok(Map.of(
            "token", token,
            "userId", user.getId(),
            "username", user.getUsername(),
            "roles", springRoles,
            "message", "Admin login successful"
        ));
    }

    /* ---------- CREATE INITIAL ADMIN (FOR TESTING) ---------- */
    @PostMapping("/admin/create")
    public ResponseEntity<?> createInitialAdmin(@RequestBody LoginRequest req) {
        System.out.println("Creating initial admin user: " + req.getEmail());
        
        // Check if admin already exists
        if (userRepository.existsByUsername(req.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Admin user already exists"));
        }
        
        User adminUser = new User();
        adminUser.setUsername(req.getEmail());
        adminUser.setEmail(req.getEmail() + "@admin.com"); // Set a dummy email
        adminUser.setPassword(encoder.encode(req.getPassword()));
        adminUser.setRoles(Set.of("ADMIN"));
        
        User savedAdmin = userRepository.save(adminUser);
        System.out.println("Admin user created with ID: " + savedAdmin.getId());
        
        return ResponseEntity.ok(Map.of(
            "message", "Admin user created successfully",
            "username", savedAdmin.getUsername(),
            "userId", savedAdmin.getId()
        ));
    }
}
