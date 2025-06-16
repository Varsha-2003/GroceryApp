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

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtil jwtUtil;

    /* ---------- SIGN‑UP ---------- */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        if (customerRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        Customer c = new Customer();
        c.setFullName(req.getCustomername());
        c.setEmail(req.getEmail());
        c.setPassword(encoder.encode(req.getPassword()));
        c.setAddress(req.getAdress());
        c.setPhone(req.getPhonenumber() != null ? req.getPhonenumber().toString() : null);

        customerRepo.save(c);
        return ResponseEntity.ok("Signup successful");
    }

    /* ---------- LOG‑IN ---------- */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Customer c = customerRepo.findByEmail(req.getEmail())
                .orElse(null);

        if (c == null || !encoder.matches(req.getPassword(), c.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String token = jwtUtil.generateToken(c.getEmail());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
