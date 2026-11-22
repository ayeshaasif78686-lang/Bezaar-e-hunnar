package com.bazaarehunar.config;

import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {
    /** Fail with a clear message after 10 seconds if MongoDB cannot be reached (instead of waiting 30 seconds). */
    @Bean
    public MongoClientSettingsBuilderCustomizer mongoTimeoutCustomizer() {
        return builder -> builder.applyToClusterSettings(cluster -> cluster.serverSelectionTimeout(10, TimeUnit.SECONDS));
    }
}
