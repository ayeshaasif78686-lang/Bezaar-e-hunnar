package com.bazaarehunar.controller;

import com.bazaarehunar.service.AdminService;
import com.bazaarehunar.service.StoreSettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/** Admin panel API. Products and orders reuse /api/products and /api/orders (already admin-capable). */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService admin;
    private final StoreSettingsService settings;

    public AdminController(AdminService admin, StoreSettingsService settings) {
        this.admin = admin;
        this.settings = settings;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(Authentication auth) { requireAdmin(auth); return admin.dashboard(); }

    @GetMapping("/notifications")
    public Map<String, Object> notifications(Authentication auth) { requireAdmin(auth); return admin.notifications(); }

    @GetMapping("/reports")
    public Map<String, Object> reports(@RequestParam(defaultValue = "month") String period, Authentication auth) {
        requireAdmin(auth);
        return admin.reports(period);
    }

    // ---- categories
    @GetMapping("/categories")
    public List<Map<String, Object>> categories(Authentication auth) { requireAdmin(auth); return admin.categories(); }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addCategory(@RequestBody Map<String, String> body, Authentication auth) {
        requireAdmin(auth);
        return admin.addCategory(body.get("name"), body.get("image"));
    }

    @PutMapping("/categories")
    public Map<String, Object> renameCategory(@RequestBody Map<String, String> body, Authentication auth) {
        requireAdmin(auth);
        return admin.renameCategory(body.get("oldName"), body.get("name"), body.get("image"));
    }

    @DeleteMapping("/categories")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@RequestParam String name, Authentication auth) {
        requireAdmin(auth);
        admin.deleteCategory(name);
    }

    // ---- customers
    @GetMapping("/customers")
    public List<Map<String, Object>> customers(@RequestParam(required = false) String q, Authentication auth) {
        requireAdmin(auth);
        return admin.customers(q);
    }

    @GetMapping("/customers/{id}")
    public Map<String, Object> customer(@PathVariable String id, Authentication auth) {
        requireAdmin(auth);
        return admin.customerDetail(id);
    }

    // ---- sellers
    @GetMapping("/sellers")
    public List<Map<String, Object>> sellers(@RequestParam(required = false) String q, Authentication auth) {
        requireAdmin(auth);
        return admin.sellers(q);
    }

    @GetMapping("/sellers/{id}")
    public Map<String, Object> seller(@PathVariable String id, Authentication auth) {
        requireAdmin(auth);
        return admin.sellerDetail(id);
    }

    @PostMapping("/sellers")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createSeller(@RequestBody Map<String, String> body, Authentication auth) {
        requireAdmin(auth);
        return admin.createSeller(body.get("name"), body.get("email"), body.get("password"), body.get("phone"));
    }

    /** Body: {"active": true|false} - works for customers and sellers. */
    @PatchMapping("/users/{id}/status")
    public Map<String, Object> userStatus(@PathVariable String id, @RequestBody Map<String, Boolean> body, Authentication auth) {
        requireAdmin(auth);
        return admin.setUserActive(id, Boolean.TRUE.equals(body.get("active")));
    }

    // ---- contact messages & custom order requests
    @GetMapping("/messages")
    public List<com.bazaarehunar.model.ContactMessage> messages(Authentication auth) { requireAdmin(auth); return admin.messages(); }

    /** Body: {"status": "NEW" | "READ" | "REPLIED"} */
    @PatchMapping("/messages/{id}")
    public com.bazaarehunar.model.ContactMessage messageStatus(@PathVariable String id, @RequestBody Map<String, String> body, Authentication auth) {
        requireAdmin(auth);
        return admin.setMessageStatus(id, body.get("status"));
    }

    @DeleteMapping("/messages/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable String id, Authentication auth) { requireAdmin(auth); admin.deleteMessage(id); }

    @GetMapping("/custom-orders")
    public List<com.bazaarehunar.model.CustomOrderRequest> customOrders(Authentication auth) { requireAdmin(auth); return admin.customOrders(); }

    /** Body: {"status": "Request received" | "In review" | "Quote sent" | "In progress" | "Completed" | "Declined"} */
    @PatchMapping("/custom-orders/{id}")
    public com.bazaarehunar.model.CustomOrderRequest customOrderStatus(@PathVariable String id, @RequestBody Map<String, String> body, Authentication auth) {
        requireAdmin(auth);
        return admin.setCustomOrderStatus(id, body.get("status"));
    }

    // ---- settings
    @GetMapping("/settings")
    public Map<String, Object> settings(Authentication auth) { requireAdmin(auth); return settings.get(); }

    @PutMapping("/settings")
    public Map<String, Object> updateSettings(@RequestBody Map<String, Object> body, Authentication auth) {
        requireAdmin(auth);
        return settings.update(body);
    }

    private void requireAdmin(Authentication auth) {
        boolean isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
    }
}
