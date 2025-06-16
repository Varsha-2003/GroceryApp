package com.app.springapp.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.springapp.entity.Feedback;
import com.app.springapp.entity.Product;
import com.app.springapp.repository.FeedbackRepository;
import com.app.springapp.repository.ProductRepository;


@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback create(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    public Feedback getById(Long id) {
        return feedbackRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
    }

    public Feedback update(Long id, Feedback feedback) {
        Feedback existing = getById(id);
        existing.setRating(feedback.getRating());
        existing.setComments(feedback.getComments());
        existing.setCustomer(feedback.getCustomer());
        existing.setProduct(feedback.getProduct());
        return feedbackRepository.save(existing);
    }

    public void delete(Long id) {
        feedbackRepository.deleteById(id);
    }
}
