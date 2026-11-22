package com.bazaarehunar.controller;

import com.bazaarehunar.model.AppUser;
import com.bazaarehunar.patterns.structural.proxy.RoleBasedDashboardProxy;
import com.bazaarehunar.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final UserService userService;
    private final RoleBasedDashboardProxy dashboardProxy;

    public DashboardController(UserService userService, RoleBasedDashboardProxy dashboardProxy) {
        this.userService = userService;
        this.dashboardProxy = dashboardProxy;
    }

    @GetMapping("/{area}")
    public Map<String, Object> dashboard(@PathVariable String area, Authentication authentication) {
        AppUser user = userService.getByEmail(authentication.getName());
        return dashboardProxy.openDashboard(user, area);
    }
}
