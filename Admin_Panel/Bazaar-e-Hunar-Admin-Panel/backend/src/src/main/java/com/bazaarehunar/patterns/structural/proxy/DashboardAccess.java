package com.bazaarehunar.patterns.structural.proxy;

import com.bazaarehunar.model.AppUser;

import java.util.Map;

public interface DashboardAccess {
    Map<String, Object> openDashboard(AppUser user, String requestedArea);
}
