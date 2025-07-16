package com.app.springapp.service;

import com.app.springapp.entity.Customer;
import com.app.springapp.repository.CustomerRepository;
import com.app.springapp.entity.User;
import com.app.springapp.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find admin user first
        User admin = userRepo.findByUsername(username).orElse(null);
        if (admin != null) {
            Set<SimpleGrantedAuthority> authorities = admin.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
            return new org.springframework.security.core.userdetails.User(
                admin.getUsername(),
                admin.getPassword(),
                authorities
            );
        }

        // If not found, try customer
        Customer customer = customerRepo.findByEmail(username).orElse(null);
        if (customer != null) {
            return new org.springframework.security.core.userdetails.User(
                customer.getEmail(),
                customer.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
            );
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
