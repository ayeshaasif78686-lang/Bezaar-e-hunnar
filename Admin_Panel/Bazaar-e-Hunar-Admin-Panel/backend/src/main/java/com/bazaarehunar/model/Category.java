package com.bazaarehunar.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/** A category created from the admin panel (collection "categories"). Product categories also exist implicitly. */
@Document(collection = "categories")
public class Category {
    @Id
    private String id;
    private String name;
    private String image;
    private Instant createdAt = Instant.now();

    public Category() { }
    public Category(String name, String image) { this.name = name; this.image = image; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
