package com.bazaarehunar.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Local JSON-backed storage for product reviews (same approach as UserRepository).
 * Reviews are saved in backend/data/reviews.json, so no SQL database is required.
 */
@Repository
public class ProductReviewRepository {

    public record StoredReview(long id, String productId, String name, int rating, String body, String date) { }

    private final List<StoredReview> reviews = new ArrayList<>();
    private final ObjectMapper objectMapper;
    private final Path filePath;
    private long lastId = 0;

    public ProductReviewRepository(ObjectMapper objectMapper,
                                   @Value("${app.data.reviews-file:data/reviews.json}") String reviewsFile) {
        this.objectMapper = objectMapper;
        this.filePath = Paths.get(reviewsFile).toAbsolutePath().normalize();
        loadFromDisk();
    }

    public synchronized List<Map<String, Object>> findByProduct(String productId) {
        List<StoredReview> matching = new ArrayList<>();
        for (StoredReview review : reviews) {
            if (review.productId().equals(productId)) {
                matching.add(review);
            }
        }
        // Newest first (same as sorting by date descending)
        matching.sort((first, second) -> second.date().compareTo(first.date()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (StoredReview review : matching) {
            result.add(toResponse(review));
        }
        return result;
    }

    public synchronized Map<String, Object> save(String productId, String name, int rating, String body) {
        StoredReview review = new StoredReview(++lastId, productId, name, rating, body, LocalDateTime.now().toString());
        reviews.add(review);
        persist();
        return toResponse(review);
    }

    private static Map<String, Object> toResponse(StoredReview review) {
        return Map.of(
                "id", review.id(),
                "name", review.name(),
                "rating", review.rating(),
                "body", review.body(),
                "date", review.date()
        );
    }

    private void loadFromDisk() {
        try {
            if (!Files.exists(filePath)) {
                return;
            }
            List<StoredReview> stored = objectMapper.readValue(
                    filePath.toFile(), new TypeReference<List<StoredReview>>() { }
            );
            reviews.addAll(stored);
            long maxId = 0;
            for (StoredReview review : reviews) {
                maxId = Math.max(maxId, review.id());
            }
            lastId = maxId;
        } catch (Exception ex) {
            System.err.println("Could not read local reviews file. Starting with no saved reviews: " + ex.getMessage());
        }
    }

    private void persist() {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), reviews);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not save product review", ex);
        }
    }
}
