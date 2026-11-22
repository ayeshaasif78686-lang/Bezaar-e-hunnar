package com.bazaarehunar.controller;

import com.bazaarehunar.dto.ProfileUpdateRequest;
import com.bazaarehunar.dto.UserResponse;
import com.bazaarehunar.model.AppUser;
import com.bazaarehunar.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        AppUser user = userService.getByEmail(authentication.getName());
        return UserResponse.from(user);
    }

    @PutMapping("/me")
    public UserResponse updateMe(@RequestBody ProfileUpdateRequest request, Authentication authentication) {
        return UserResponse.from(userService.updateProfile(authentication.getName(), request));
    }
}
