package com.bazaarehunar.dto;

import com.bazaarehunar.model.Order;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.ArrayList;
import java.util.List;

/** Checkout payload. Card details are never sent to or stored by the backend. */
public class OrderRequest {
    private List<Item> items = new ArrayList<>();
    private Order.Address address;
    private String delivery;       // "standard" or "express"
    private String paymentMethod;  // e.g. "Cash on Delivery"
    private String coupon;

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
    public Order.Address getAddress() { return address; }
    public void setAddress(Order.Address address) { this.address = address; }
    public String getDelivery() { return delivery; }
    public void setDelivery(String delivery) { this.delivery = delivery; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getCoupon() { return coupon; }
    public void setCoupon(String coupon) { this.coupon = coupon; }

    public static class Item {
        @JsonAlias({"id"})
        private String productId;
        private String name;
        private String image;
        private int price;
        private int quantity;

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}
