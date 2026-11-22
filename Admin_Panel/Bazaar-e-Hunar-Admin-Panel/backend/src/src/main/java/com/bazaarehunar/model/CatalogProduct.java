package com.bazaarehunar.model;

public class CatalogProduct {
    private String id;
    private String name;
    private String category;
    private int price;
    private double rating;
    private int reviews;
    private String badge;
    private String image;
    private String artisan;
    private String artisanId;
    private String region;
    private String material;
    private String description;
    private boolean customizable;
    private String styleKey;

    public CatalogProduct() { }

    public CatalogProduct(String id, String name, String category, int price, double rating, int reviews,
                          String badge, String image, String artisan, String artisanId, String region,
                          String material, String description, boolean customizable, String styleKey) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.rating = rating;
        this.reviews = reviews;
        this.badge = badge;
        this.image = image;
        this.artisan = artisan;
        this.artisanId = artisanId;
        this.region = region;
        this.material = material;
        this.description = description;
        this.customizable = customizable;
        this.styleKey = styleKey;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getReviews() { return reviews; }
    public void setReviews(int reviews) { this.reviews = reviews; }
    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getArtisan() { return artisan; }
    public void setArtisan(String artisan) { this.artisan = artisan; }
    public String getArtisanId() { return artisanId; }
    public void setArtisanId(String artisanId) { this.artisanId = artisanId; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCustomizable() { return customizable; }
    public void setCustomizable(boolean customizable) { this.customizable = customizable; }
    public String getStyleKey() { return styleKey; }
    public void setStyleKey(String styleKey) { this.styleKey = styleKey; }
}
