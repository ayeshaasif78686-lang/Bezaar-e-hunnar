package com.bazaarehunar.controller;

import com.bazaarehunar.repository.ProductReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ProductReviewController {
    private final ProductReviewRepository reviewRepository;

    public ProductReviewController(ProductReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public List<Map<String, Object>> list(@PathVariable String productId) {
        return reviewRepository.findByProduct(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@PathVariable String productId, @RequestBody ReviewInput input) {
        if (input.name() == null || input.name().isBlank() ||
            input.body() == null || input.body().trim().length() < 10 ||
            input.body().length() > 2000 || input.rating() < 1 || input.rating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a name, a 10-2000 character review and a rating from 1 to 5.");
        }
        return reviewRepository.save(productId, input.name().trim(), input.rating(), input.body().trim());
    }

    public record ReviewInput(String name, int rating, String body) {}
}
