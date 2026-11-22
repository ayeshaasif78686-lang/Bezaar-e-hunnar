package com.bazaarehunar.repository;

import com.bazaarehunar.model.AppUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local JSON-backed repository used by default for the university demo.
 * It keeps authentication fully functional without requiring MongoDB Atlas,
 * an internet connection, IP allow-list changes, or TLS configuration.
 */
@Repository
public class UserRepository {
    private final Map<String, AppUser> usersByEmail = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final Path filePath;

    public UserRepository(ObjectMapper objectMapper,
                          @Value("${app.data.users-file:data/users.json}") String usersFile) {
        this.objectMapper = objectMapper;
        this.filePath = Paths.get(usersFile).toAbsolutePath().normalize();
        loadFromDisk();
    }

    public Optional<AppUser> findByEmailIgnoreCase(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(usersByEmail.get(normalize(email)));
    }

    public boolean existsByEmailIgnoreCase(String email) {
        return email != null && usersByEmail.containsKey(normalize(email));
    }

    public synchronized AppUser save(AppUser user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }
        user.setEmail(normalize(user.getEmail()));
        usersByEmail.put(user.getEmail(), user);
        persist();
        return user;
    }

    public List<AppUser> findAll() {
        List<AppUser> users = new ArrayList<>(usersByEmail.values());
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

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }

    private void loadFromDisk() {
        try {
            if (!Files.exists(filePath)) {
                return;
            }
            List<AppUser> users = objectMapper.readValue(
                    filePath.toFile(), new TypeReference<List<AppUser>>() { }
            );
            for (AppUser user : users) {
                if (user.getEmail() != null) {
                    usersByEmail.put(normalize(user.getEmail()), user);
                }
            }
        } catch (Exception ex) {
            System.err.println("Could not read local user database. Starting with an empty store: " + ex.getMessage());
        }
    }

    private void persist() {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<AppUser> users = new ArrayList<>(usersByEmail.values());
            users.sort((first, second) -> first.getEmail().compareTo(second.getEmail()));
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), users);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not save local user database", ex);
        }
    }
}
