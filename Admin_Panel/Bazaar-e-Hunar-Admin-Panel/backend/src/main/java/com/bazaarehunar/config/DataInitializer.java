package com.bazaarehunar.config;

import com.bazaarehunar.model.AppUser;
import com.bazaarehunar.model.ProductReview;
import com.bazaarehunar.repository.ProductReviewRepository;
import com.bazaarehunar.repository.UserRepository;
import com.bazaarehunar.service.ArtisanService;
import com.bazaarehunar.service.CatalogService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Prepares MongoDB on startup:
 * 1) seeds the built-in products and artisans when those collections are empty;
 * 2) copies users/reviews from the old local JSON files (data/users.json, data/reviews.json)
 *    when the matching MongoDB collection is still empty, so no existing account is lost.
 * Runs before AdminSeeder.
 */
@Component
@Order(2)
public class DataInitializer implements CommandLineRunner {
    private final CatalogService catalogService;
    private final ArtisanService artisanService;
    private final UserRepository userRepository;
    private final ProductReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;
    private final Path usersFile;
    private final Path reviewsFile;

    public DataInitializer(CatalogService catalogService,
                           ArtisanService artisanService,
                           UserRepository userRepository,
                           ProductReviewRepository reviewRepository,
                           ObjectMapper objectMapper,
                           @Value("${app.data.users-file:data/users.json}") String usersFile,
                           @Value("${app.data.reviews-file:data/reviews.json}") String reviewsFile) {
        this.catalogService = catalogService;
        this.artisanService = artisanService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.objectMapper = objectMapper;
        this.usersFile = Paths.get(usersFile).toAbsolutePath().normalize();
        this.reviewsFile = Paths.get(reviewsFile).toAbsolutePath().normalize();
    }

    @Override
    public void run(String... args) {
        catalogService.seedDatabaseIfEmpty();
        artisanService.seedIfEmpty();
        migrateUsers();
        migrateReviews();
    }

    private void migrateUsers() {
        if (!Files.exists(usersFile) || userRepository.count() > 0) {
            return;
        }
        try {
            List<AppUser> users = objectMapper.readValue(usersFile.toFile(), new TypeReference<List<AppUser>>() { });
            int imported = 0;
            for (AppUser user : users) {
                if (user.getEmail() != null && !user.getEmail().isBlank()) {
                    userRepository.save(user);
                    imported++;
                }
            }
            System.out.println("Copied " + imported + " user(s) from " + usersFile.getFileName() + " into MongoDB.");
        } catch (Exception ex) {
            System.err.println("Could not copy users from " + usersFile + ": " + ex.getMessage());
        }
    }

    private void migrateReviews() {
        if (!Files.exists(reviewsFile) || reviewRepository.count() > 0) {
            return;
        }
        try {
            List<ProductReview> reviews = objectMapper.readValue(reviewsFile.toFile(), new TypeReference<List<ProductReview>>() { });
            reviewRepository.importLegacy(reviews);
            System.out.println("Copied " + reviews.size() + " review(s) from " + reviewsFile.getFileName() + " into MongoDB.");
        } catch (Exception ex) {
            System.err.println("Could not copy reviews from " + reviewsFile + ": " + ex.getMessage());
        }
    }
}
