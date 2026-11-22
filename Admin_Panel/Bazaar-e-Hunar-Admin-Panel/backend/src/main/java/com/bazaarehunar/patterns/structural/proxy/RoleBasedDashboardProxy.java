package com.bazaarehunar.patterns.structural.proxy;

import com.bazaarehunar.model.AppUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class RoleBasedDashboardProxy implements DashboardAccess {
    private static final Set<String> VALID_AREAS = Set.of("buyer", "seller", "admin");
    private final RealDashboardService realDashboardService;

    public RoleBasedDashboardProxy(RealDashboardService realDashboardService) {
        this.realDashboardService = realDashboardService;
    }

    @Override
    public Map<String, Object> openDashboard(AppUser user, String requestedArea) {
        String area = requestedArea == null ? "" : requestedArea.trim().toLowerCase(Locale.ROOT);
        if (!VALID_AREAS.contains(area)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown dashboard area");
        }

        String allowedArea = user.getRole().name().toLowerCase(Locale.ROOT);
        if (!allowedArea.equals(area)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Proxy blocked access. Your role can only open the " + allowedArea + " dashboard."
            );
        }

        return realDashboardService.openDashboard(user, area);
    }
}
