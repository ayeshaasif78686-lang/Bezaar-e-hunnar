package com.bazaarehunar.config;

import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/** Runs first on startup and gives a readable error if MongoDB cannot be reached. */
@Component
@Order(1)
public class MongoStartupCheck implements CommandLineRunner {
    private final MongoTemplate mongoTemplate;

    public MongoStartupCheck(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            mongoTemplate.executeCommand(new Document("ping", 1));
            System.out.println("MongoDB connected. Database: " + mongoTemplate.getDb().getName());
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Could not connect to MongoDB. Check MONGODB_URI in backend/.env, your internet connection, "
                            + "and (for Atlas) Network Access -> add your current IP address. Details: " + ex.getMessage(), ex);
        }
    }
}
