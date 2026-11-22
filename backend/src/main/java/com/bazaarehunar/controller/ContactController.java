package com.bazaarehunar.controller;

import com.bazaarehunar.model.ContactMessage;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Messages from the Contact page, stored in MongoDB (collection "contact_messages"). */
@RestController
@RequestMapping("/api/contact")
public class ContactController {
    private final MongoTemplate mongoTemplate;

    public ContactController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@RequestBody ContactMessage input) {
        if (isBlank(input.getName()) || isBlank(input.getEmail()) || isBlank(input.getSubject()) || isBlank(input.getMessage())) {
            throw new IllegalArgumentException("Name, email, subject and message are required");
        }
        if (input.getMessage().length() > 5000) {
            throw new IllegalArgumentException("Message is too long (maximum 5000 characters)");
        }
        ContactMessage message = new ContactMessage();
        message.setName(input.getName().trim());
        message.setEmail(input.getEmail().trim().toLowerCase());
        message.setSubject(input.getSubject().trim());
        message.setMessage(input.getMessage().trim());
        message.setAgree(input.isAgree());
        message.setStatus("NEW");
        message.setCreatedAt(Instant.now());
        ContactMessage saved = mongoTemplate.insert(message);
        return Map.of("id", saved.getId(), "status", "received");
    }

    /** All messages (admin only). */
    @GetMapping
    public List<ContactMessage> all(Authentication authentication) {
        if (authentication == null
                || authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
        }
        return mongoTemplate.find(new Query().with(Sort.by(Sort.Direction.DESC, "createdAt")), ContactMessage.class);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
