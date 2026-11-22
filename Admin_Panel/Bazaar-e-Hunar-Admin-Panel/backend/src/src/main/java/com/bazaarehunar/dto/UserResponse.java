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

    public static UserResponse from(AppUser user) {
        UserResponse response = new UserResponse();
        response.id = user.getId();
        response.name = user.getName();
        response.email = user.getEmail();
        response.role = user.getRole();
        response.createdAt = user.getCreatedAt();
        return response;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }
}
