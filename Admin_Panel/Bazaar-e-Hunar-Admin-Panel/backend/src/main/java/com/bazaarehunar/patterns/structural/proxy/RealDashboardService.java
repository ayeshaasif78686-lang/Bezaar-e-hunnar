package com.bazaarehunar.patterns.structural.proxy;

import com.bazaarehunar.model.AppUser;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RealDashboardService implements DashboardAccess {
    @Override
    public Map<String, Object> openDashboard(AppUser user, String requestedArea) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("title", requestedArea.substring(0, 1).toUpperCase() + requestedArea.substring(1) + " Dashboard");
        payload.put("user", user.getName());
        payload.put("role", user.getRole().name());

        switch (requestedArea) {
            case "buyer" -> payload.put("features", List.of("Browse products", "Wishlist", "Orders", "Haggle offers"));
            case "seller" -> payload.put("features", List.of("Add products", "Manage inventory", "Orders", "Haggle requests"));
            case "admin" -> payload.put("features", List.of("Manage users", "Moderate products", "Reports", "Platform settings"));
            default -> payload.put("features", List.of());
        }
        return payload;
    }
}
