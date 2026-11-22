package com.bazaarehunar.service;

import com.bazaarehunar.dto.ProfileUpdateRequest;
import com.bazaarehunar.model.AppUser;
import com.bazaarehunar.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public AppUser updateProfile(String email, ProfileUpdateRequest request) {
        AppUser user = getByEmail(email);
        if (request.getName() != null && !request.getName().isBlank()) {
            String name = request.getName().trim();
            if (name.length() < 2 || name.length() > 80) {
                throw new IllegalArgumentException("Name must be between 2 and 80 characters");
            }
            user.setName(name);
        }
        user.setPhone(clean(request.getPhone()));
        user.setCity(clean(request.getCity()));
        user.setProvince(clean(request.getProvince()));
        user.setAddress(clean(request.getAddress()));
        user.setPostalCode(clean(request.getPostalCode()));
        return userRepository.save(user);
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
