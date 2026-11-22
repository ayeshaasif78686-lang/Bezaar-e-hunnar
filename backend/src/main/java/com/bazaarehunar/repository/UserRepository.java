package com.bazaarehunar.repository;

import com.bazaarehunar.model.AppUser;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * MongoDB-backed repository for users (collection "users").
 * The public methods are the same as the old local JSON version, so the rest of the app is unchanged.
 */
@Repository
public class UserRepository {
    private final MongoTemplate mongoTemplate;

    public UserRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<AppUser> findByEmailIgnoreCase(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(mongoTemplate.findOne(emailQuery(email), AppUser.class));
    }

    public Optional<AppUser> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(mongoTemplate.findById(id, AppUser.class));
    }

    public boolean existsByEmailIgnoreCase(String email) {
        return email != null && mongoTemplate.exists(emailQuery(email), AppUser.class);
    }

    public AppUser save(AppUser user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }
        user.setEmail(normalize(user.getEmail()));
        return mongoTemplate.save(user);
    }

    public List<AppUser> findAll() {
        List<AppUser> users = new ArrayList<>(mongoTemplate.findAll(AppUser.class));
        users.sort((first, second) -> {
            Instant firstCreated = first.getCreatedAt();
            Instant secondCreated = second.getCreatedAt();
            if (firstCreated == null && secondCreated == null) {
                return 0;
            }
            if (firstCreated == null) {
                return 1; // nulls last
            }
            if (secondCreated == null) {
                return -1;
            }
            return firstCreated.compareTo(secondCreated);
        });
        return users;
    }

    public long count() {
        return mongoTemplate.count(new Query(), AppUser.class);
    }

    private Query emailQuery(String email) {
        return new Query(Criteria.where("email").is(normalize(email)));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
