package com.bazaarehunar.controller;

import com.bazaarehunar.model.CustomOrderRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

/** Custom order requests from the "Custom Orders" page, stored in MongoDB (collection "custom_orders"). */
@RestController
@RequestMapping("/api/custom-orders")
public class CustomOrderController {
    private final MongoTemplate mongoTemplate;

    public CustomOrderController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /** Open to guests too (like the page itself). If a token is sent, the request is linked to that user. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomOrderRequest create(@RequestBody CustomOrderRequest input, Authentication authentication) {
        if (isBlank(input.getName()) || isBlank(input.getEmail()) || isBlank(input.getType()) || isBlank(input.getDetails())) {
            throw new IllegalArgumentException("Name, email, product type and details are required");
        }
        if (!input.isConsent()) {
            throw new IllegalArgumentException("Please accept the Terms & Conditions and Privacy Policy");
        }
        CustomOrderRequest request = new CustomOrderRequest();
        request.setReference("CO-" + System.currentTimeMillis());
        request.setName(input.getName().trim());
        request.setEmail(input.getEmail().trim().toLowerCase());
        request.setType(input.getType().trim());
        request.setStyle(input.getStyle());
        request.setDetails(input.getDetails().trim());
        request.setBudget(input.getBudget());
        request.setConsent(true);
        request.setUserEmail(isLoggedIn(authentication) ? authentication.getName().toLowerCase() : null);
        request.setStatus("Request received");
        request.setCreatedAt(Instant.now());
        return mongoTemplate.insert(request);
    }

    @GetMapping("/my")
    public List<CustomOrderRequest> mine(Authentication authentication) {
        String email = authentication.getName().toLowerCase();
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("userEmail").is(email), Criteria.where("email").is(email)))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, CustomOrderRequest.class);
    }

    @GetMapping
    public List<CustomOrderRequest> all(Authentication authentication) {
        if (!isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
        }
        return mongoTemplate.find(new Query().with(Sort.by(Sort.Direction.DESC, "createdAt")), CustomOrderRequest.class);
    }

    private boolean isLoggedIn(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
