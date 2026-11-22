package com.bazaarehunar.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    @Indexed(unique = true)
    private String orderNumber;
    private String userId;
    @Indexed
    private String userEmail;
    private List<Item> items = new ArrayList<>();
    private Address address;
    private String delivery;
    private String deliveryLabel;
    private String estimatedDelivery;
    private String paymentMethod;
    private String coupon;
    private int subtotal;
    private int shipping;
    private int discount;
    private int tax;
    private int total;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public String getDelivery() { return delivery; }
    public void setDelivery(String delivery) { this.delivery = delivery; }
    public String getDeliveryLabel() { return deliveryLabel; }
    public void setDeliveryLabel(String deliveryLabel) { this.deliveryLabel = deliveryLabel; }
    public String getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getCoupon() { return coupon; }
    public void setCoupon(String coupon) { this.coupon = coupon; }
    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }
    public int getShipping() { return shipping; }
    public void setShipping(int shipping) { this.shipping = shipping; }
    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }
    public int getTax() { return tax; }
    public void setTax(int tax) { this.tax = tax; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Item {
        private String productId;
        private String name;
        private String image;
        private String artisan;
        private String artisanId;
        private int price;
        private int quantity;

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public String getArtisan() { return artisan; }
        public void setArtisan(String artisan) { this.artisan = artisan; }
        public String getArtisanId() { return artisanId; }
        public void setArtisanId(String artisanId) { this.artisanId = artisanId; }
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class Address {
        private String fullName;
        private String phone;
        private String address;
        private String city;
        private String province;
        private String postalCode;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    }
}
