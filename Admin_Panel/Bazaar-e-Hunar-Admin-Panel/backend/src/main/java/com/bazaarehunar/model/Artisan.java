package com.bazaarehunar.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "artisans")
public class Artisan {
    @Id
    private String id;
    private String name;
    private String role;
    private String city;
    private String years;
    private String sold;
    private double rating;
    private int reviews;
    private String quote;
    private String bio;
    private String image;

    public Artisan() { }

    public Artisan(String id, String name, String role, String city, String years, String sold, double rating,
                   int reviews, String quote, String bio, String image) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.city = city;
        this.years = years;
        this.sold = sold;
        this.rating = rating;
        this.reviews = reviews;
        this.quote = quote;
        this.bio = bio;
        this.image = image;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getYears() { return years; }
    public void setYears(String years) { this.years = years; }
    public String getSold() { return sold; }
    public void setSold(String sold) { this.sold = sold; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getReviews() { return reviews; }
    public void setReviews(int reviews) { this.reviews = reviews; }
    public String getQuote() { return quote; }
    public void setQuote(String quote) { this.quote = quote; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
