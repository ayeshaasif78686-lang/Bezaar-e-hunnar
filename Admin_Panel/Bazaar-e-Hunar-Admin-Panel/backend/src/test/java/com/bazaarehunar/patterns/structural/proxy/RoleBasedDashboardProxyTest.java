package com.bazaarehunar.patterns.structural.proxy;

import com.bazaarehunar.model.AppUser;
import com.bazaarehunar.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoleBasedDashboardProxyTest {

    @Test
    void buyerCanOpenBuyerDashboard() {
        RealDashboardService real = new RealDashboardService();
        RoleBasedDashboardProxy proxy = new RoleBasedDashboardProxy(real);
        AppUser buyer = new AppUser("Ayesha", "buyer@example.com", "x", Role.BUYER);

        assertEquals("BUYER", proxy.openDashboard(buyer, "buyer").get("role"));
    }

    @Test
    void buyerCannotOpenAdminDashboard() {
        RealDashboardService real = new RealDashboardService();
        RoleBasedDashboardProxy proxy = new RoleBasedDashboardProxy(real);
        AppUser buyer = new AppUser("Ayesha", "buyer@example.com", "x", Role.BUYER);

        assertThrows(ResponseStatusException.class, () -> proxy.openDashboard(buyer, "admin"));
    }
}
