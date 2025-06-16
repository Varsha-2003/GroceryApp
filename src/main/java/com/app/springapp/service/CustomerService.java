package com.app.springapp.service;


import java.util.List;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.app.springapp.entity.Customer;
import com.app.springapp.entity.Product;
import com.app.springapp.repository.CustomerRepository;
import com.app.springapp.repository.ProductRepository;


@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Customer update(Long id, Customer customer) {
        Customer existing = getById(id);
        existing.setFullName(customer.getFullName());
        existing.setEmail(customer.getEmail());
        existing.setPhone(customer.getPhone());
        existing.setPassword(customer.getPassword());
        existing.setAddress(customer.getAddress());
        return customerRepository.save(existing);
    }

    public void delete(Long id) {
        customerRepository.deleteById(id);


    }
 
}
