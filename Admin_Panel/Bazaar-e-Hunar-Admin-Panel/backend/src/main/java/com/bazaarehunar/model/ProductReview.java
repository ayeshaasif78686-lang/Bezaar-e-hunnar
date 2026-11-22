package com.bazaarehunar.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_reviews")
public class ProductReview {
    @Id
    private long id;
    @Indexed
    private String productId;
    private String name;
    private int rating;
    private String body;
    private String date;

    public ProductReview() { }

    public ProductReview(long id, String productId, String name, int rating, String body, String date) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.rating = rating;
        this.body = body;
        this.date = date;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
