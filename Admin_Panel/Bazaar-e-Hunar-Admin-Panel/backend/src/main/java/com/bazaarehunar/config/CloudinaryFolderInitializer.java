package com.bazaarehunar.config;

import com.bazaarehunar.patterns.structural.composite.CategoryComponent;
import com.bazaarehunar.service.CatalogService;
import com.bazaarehunar.service.CloudinaryAssetSyncService;
import com.bazaarehunar.service.CloudinaryUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * On startup: makes sure every category has its own folder under bazaar-e-hunar/products in Cloudinary, then
 * (in the background, so the API starts immediately) uploads the product images from the frontend assets folder.
 */
@Component
@Order(3)
public class CloudinaryFolderInitializer implements CommandLineRunner {
    private final CatalogService catalogService;
    private final CloudinaryUploadService cloudinaryUploadService;
    private final CloudinaryAssetSyncService syncService;
    private final boolean syncOnStartup;

    public CloudinaryFolderInitializer(CatalogService catalogService, CloudinaryUploadService cloudinaryUploadService,
                                       CloudinaryAssetSyncService syncService,
                                       @Value("${app.cloudinary.sync-on-startup:true}") boolean syncOnStartup) {
        this.catalogService = catalogService;
        this.cloudinaryUploadService = cloudinaryUploadService;
        this.syncService = syncService;
        this.syncOnStartup = syncOnStartup;
    }

    @Override
    public void run(String... args) {
        int created = 0;
        for (CategoryComponent category : catalogService.getCategoryTree().children()) {
            if (cloudinaryUploadService.ensureCategoryFolder(category.name())) created++;
        }
        System.out.println("Cloudinary category folders ensured: " + created);

        if (syncOnStartup && cloudinaryUploadService.isConfigured()) {
            Thread worker = new Thread(() -> System.out.println("Cloudinary image sync: " + syncService.sync()), "cloudinary-asset-sync");
            worker.setDaemon(true);
            worker.start();
        }
    }
}
