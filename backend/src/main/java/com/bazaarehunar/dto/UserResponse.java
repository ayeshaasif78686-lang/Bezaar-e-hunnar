package com.bazaarehunar.dto;

import com.bazaarehunar.model.AppUser;
import com.bazaarehunar.model.Role;

import java.time.Instant;

public class UserResponse {
    private String id;
    private String name;
    private String email;
    private Role role;
    private Instant createdAt;
    private String phone;
    private String city;
    private String province;
    private String address;
    private String postalCode;

    public static UserResponse from(AppUser user) {
        UserResponse response = new UserResponse();
        response.id = user.getId();
        response.name = user.getName();
        response.email = user.getEmail();
        response.role = user.getRole();
        response.createdAt = user.getCreatedAt();
        response.phone = user.getPhone();
        response.city = user.getCity();
        response.province = user.getProvince();
        response.address = user.getAddress();
        response.postalCode = user.getPostalCode();
        return response;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }
    public String getPhone() { return phone; }
    public String getCity() { return city; }
    public String getProvince() { return province; }
    public String getAddress() { return address; }
    public String getPostalCode() { return postalCode; }
}
