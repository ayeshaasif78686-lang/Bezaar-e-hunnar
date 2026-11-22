package com.bazaarehunar.repository;

import com.bazaarehunar.model.ProductReview;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MongoDB-backed storage for product reviews (collection "product_reviews").
 * Review ids stay numeric (1, 2, 3 ...) exactly like before, using a small counter document.
 */
@Repository
public class ProductReviewRepository {
    private static final String COUNTERS = "counters";
    private static final String COUNTER_NAME = "product_reviews";

    private final MongoTemplate mongoTemplate;

    public ProductReviewRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Map<String, Object>> findByProduct(String productId) {
        Query query = new Query(Criteria.where("productId").is(productId))
                .with(Sort.by(Sort.Direction.DESC, "date"));
        List<ProductReview> reviews = mongoTemplate.find(query, ProductReview.class);

        List<Map<String, Object>> result = new ArrayList<>();
        for (ProductReview review : reviews) {
            result.add(toResponse(review));
        }
        return result;
    }

    public Map<String, Object> save(String productId, String name, int rating, String body) {
        ProductReview review = new ProductReview(nextId(), productId, name, rating, body, LocalDateTime.now().toString());
        mongoTemplate.insert(review);
        return toResponse(review);
    }

    public long count() {
        return mongoTemplate.count(new Query(), ProductReview.class);
    }

    /** Copies reviews from the old reviews.json into MongoDB and moves the id counter past them. */
    public void importLegacy(List<ProductReview> legacyReviews) {
        if (legacyReviews == null || legacyReviews.isEmpty()) {
            return;
        }
        long maxId = 0;
        for (ProductReview review : legacyReviews) {
            maxId = Math.max(maxId, review.getId());
        }
        mongoTemplate.insert(legacyReviews, ProductReview.class);
        mongoTemplate.getCollection(COUNTERS).updateOne(
                Filters.eq("_id", COUNTER_NAME),
                Updates.max("seq", maxId),
                new UpdateOptions().upsert(true));
    }

    private long nextId() {
        Document counter = mongoTemplate.getCollection(COUNTERS).findOneAndUpdate(
                Filters.eq("_id", COUNTER_NAME),
                Updates.inc("seq", 1L),
                new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
        if (counter == null) {
            return 1L;
        }
        return ((Number) counter.get("seq")).longValue();
    }

    private static Map<String, Object> toResponse(ProductReview review) {
        return Map.of(
                "id", review.getId(),
                "name", review.getName(),
                "rating", review.getRating(),
                "body", review.getBody(),
                "date", review.getDate()
        );
    }
}
