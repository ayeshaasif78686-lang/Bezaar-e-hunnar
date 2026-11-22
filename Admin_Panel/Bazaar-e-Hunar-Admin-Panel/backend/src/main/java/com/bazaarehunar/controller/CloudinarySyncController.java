package com.bazaarehunar.controller;

import com.bazaarehunar.service.CloudinaryAssetSyncService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/cloudinary")
public class CloudinarySyncController {
    private final CloudinaryAssetSyncService syncService;

    public CloudinarySyncController(CloudinaryAssetSyncService syncService) {
        this.syncService = syncService;
    }

    /** Admin only: uploads any not-yet-uploaded product images from the frontend assets folder to Cloudinary. */
    @PostMapping("/sync-products")
    public Map<String, Object> syncProducts(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
        }
        return syncService.sync();
    }
}
