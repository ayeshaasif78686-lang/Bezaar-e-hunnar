package com.bazaarehunar.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "custom_orders")
public class CustomOrderRequest {
    @Id
    private String id;
    @Indexed
    private String reference;
    private String name;
    @Indexed
    private String email;
    private String type;
    private String style;
    private String details;
    private String budget;
    private boolean consent;
    private String userEmail;
    private String status;
    private Instant createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }
    public boolean isConsent() { return consent; }
    public void setConsent(boolean consent) { this.consent = consent; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
