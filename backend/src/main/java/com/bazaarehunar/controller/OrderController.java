package com.bazaarehunar.controller;

import com.bazaarehunar.dto.OrderRequest;
import com.bazaarehunar.model.AppUser;
import com.bazaarehunar.model.Order;
import com.bazaarehunar.service.OrderService;
import com.bazaarehunar.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /** Places an order for the logged-in user. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody OrderRequest request, Authentication authentication) {
        AppUser user = userService.getByEmail(authentication.getName());
        return orderService.create(request, user);
    }

    /** Orders of the logged-in user, newest first. */
    @GetMapping("/my")
    public List<Order> mine(Authentication authentication) {
        return orderService.findByUser(authentication.getName().trim().toLowerCase());
    }

    /** All orders (admin only). */
    @GetMapping
    public List<Order> all(Authentication authentication) {
        requireAdmin(authentication);
        return orderService.findAll();
    }

    @GetMapping("/{orderNumber}")
    public Order find(@PathVariable String orderNumber, Authentication authentication) {
        Order order = orderService.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        boolean owner = authentication.getName().equalsIgnoreCase(order.getUserEmail());
        if (!owner && !isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view this order");
        }
        return order;
    }

    /** Visitor pattern: receipt lines + totals for one order (owner or admin). */
    @GetMapping("/{orderNumber}/receipt")
    public Map<String, Object> receipt(@PathVariable String orderNumber, Authentication authentication) {
        return orderService.receipt(find(orderNumber, authentication));
    }

    /** Observer / Bridge patterns: live counters and messages recorded by the order flow (admin only). */
    @GetMapping("/activity")
    public Map<String, Object> activity(Authentication authentication) {
        requireAdmin(authentication);
        return orderService.activity();
    }

    /** Admin updates the order status: PLACED, PROCESSING, SHIPPED, DELIVERED or CANCELLED. */
    @PatchMapping("/{orderNumber}/status")
    public Order updateStatus(@PathVariable String orderNumber, @RequestBody Map<String, String> body,
                              Authentication authentication) {
        requireAdmin(authentication);
        return orderService.updateStatus(orderNumber, body.get("status"));
    }

    private void requireAdmin(Authentication authentication) {
        if (!isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
