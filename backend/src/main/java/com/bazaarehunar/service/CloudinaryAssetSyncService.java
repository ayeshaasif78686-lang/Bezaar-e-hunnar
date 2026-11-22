package com.bazaarehunar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * Uploads the product images found in the frontend assets folder (products/<category>/<file>) to Cloudinary,
 * one folder per category: bazaar-e-hunar/products/<category>/. Uploaded files are remembered in
 * data/cloudinary-synced.json, so running it again only uploads files that are new.
 */
@Service
public class CloudinaryAssetSyncService {
    private static final String ROOT_FOLDER = "bazaar-e-hunar/products";
    private static final List<String> EXTENSIONS = List.of("jpg", "jpeg", "png", "webp", "gif", "avif");

    private final CloudinaryUploadService uploadService;
    private final ObjectMapper objectMapper;
    private final String configuredDir;
    private final Path markerFile = Paths.get("data/cloudinary-synced.json").toAbsolutePath().normalize();
    private final AtomicBoolean running = new AtomicBoolean(false);

    public CloudinaryAssetSyncService(CloudinaryUploadService uploadService, ObjectMapper objectMapper,
                                      @Value("${app.cloudinary.assets-dir:}") String configuredDir) {
        this.uploadService = uploadService;
        this.objectMapper = objectMapper;
        this.configuredDir = configuredDir == null ? "" : configuredDir.trim();
    }

    public boolean isRunning() { return running.get(); }

    /** Finds the assets/products folder, or null when it cannot be found. */
    public Path findProductsDir() {
        List<Path> candidates = new ArrayList<>();
        if (!configuredDir.isBlank()) candidates.add(Paths.get(configuredDir));
        candidates.add(Paths.get("../frontend/public/assets"));
        candidates.add(Paths.get("../frontend/src/assets"));
        candidates.add(Paths.get("../frontend/assets"));
        candidates.add(Paths.get("assets"));
        candidates.add(Paths.get("../assets"));
        for (Path candidate : candidates) {
            Path normalized = candidate.toAbsolutePath().normalize();
            if (Files.isDirectory(normalized.resolve("products"))) return normalized.resolve("products");
            if (normalized.getFileName() != null && normalized.getFileName().toString().equals("products") && Files.isDirectory(normalized)) {
                return normalized;
            }
        }
        return null;
    }

    public Map<String, Object> sync() {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!uploadService.isConfigured()) {
            result.put("status", "skipped");
            result.put("reason", "Cloudinary is not configured (CLOUDINARY_* values missing)");
            return result;
        }
        Path productsDir = findProductsDir();
        if (productsDir == null) {
            result.put("status", "skipped");
            result.put("reason", "assets/products folder not found. Set CLOUDINARY_ASSETS_DIR in backend/.env");
            return result;
        }
        if (!running.compareAndSet(false, true)) {
            result.put("status", "already-running");
            return result;
        }
        int uploaded = 0, skipped = 0, failed = 0;
        Map<String, Integer> perCategory = new LinkedHashMap<>();
        try {
            Map<String, String> done = loadMarker();
            List<Path> categories;
            try (Stream<Path> stream = Files.list(productsDir)) {
                categories = stream.filter(Files::isDirectory).sorted().toList();
            }
            for (Path category : categories) {
                String slug = category.getFileName().toString();
                uploadService.ensureCategoryFolder(slug);
                List<Path> files;
                try (Stream<Path> stream = Files.list(category)) {
                    files = stream.filter(Files::isRegularFile).filter(this::isImage)
                            .sorted(Comparator.comparing((Path p) -> p.getFileName().toString(), this::naturalCompare)).toList();
                }
                int count = 0;
                for (Path file : files) {
                    String name = file.getFileName().toString();
                    String key = slug + "/" + name;
                    if (done.containsKey(key)) { skipped++; continue; }
                    try {
                        String base = name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : name;
                        String url = uploadService.uploadBytes(Files.readAllBytes(file), name, ROOT_FOLDER + "/" + slug,
                                slug + "-" + base.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]", "-"));
                        done.put(key, url);
                        uploaded++;
                        count++;
                        if (uploaded % 10 == 0) saveMarker(done);
                    } catch (Exception exception) {
                        failed++;
                        System.err.println("Cloudinary sync failed for " + key + ": " + exception.getMessage());
                    }
                }
                perCategory.put(slug, count);
            }
            saveMarker(done);
            result.put("frontendMapFile", writeFrontendMap(done, productsDir));
        } catch (IOException exception) {
            result.put("error", exception.getMessage());
        } finally {
            running.set(false);
        }
        result.put("status", "done");
        result.put("assetsFolder", productsDir.toString());
        result.put("uploaded", uploaded);
        result.put("alreadyUploaded", skipped);
        result.put("failed", failed);
        result.put("uploadedPerCategory", perCategory);
        return result;
    }

    /**
     * Rewrites frontend public/cloudinary-product-images.js (window.__BAZAAR_CLOUDINARY_IMAGES) so the website can
     * use the Cloudinary URL of every uploaded image. Keys are the local image paths, e.g.
     * "/assets/products/ajrak-shawl/1.jpg". Returns the file path, or a note when it was not written.
     */
    private String writeFrontendMap(Map<String, String> done, Path productsDir) {
        try {
            Path publicDir = productsDir.getParent() == null ? null : productsDir.getParent().getParent();
            if (publicDir == null || done.isEmpty()) return "not written";
            Path target = publicDir.resolve("cloudinary-product-images.js");
            if (!Files.exists(target) && !publicDir.getFileName().toString().equals("public")) return "not written";
            Map<String, String> map = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : done.entrySet()) {
                map.put("/assets/products/" + entry.getKey(), entry.getValue());
                map.put("assets/products/" + entry.getKey(), entry.getValue());
            }
            String content = "// Generated by the backend Cloudinary sync. Do not edit by hand.\n"
                    + "window.__BAZAAR_CLOUDINARY_IMAGES = "
                    + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map) + ";\n";
            Files.writeString(target, content);
            return target.toString();
        } catch (Exception exception) {
            System.err.println("Could not write frontend Cloudinary map: " + exception.getMessage());
            return "failed: " + exception.getMessage();
        }
    }

    private boolean isImage(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        int dot = name.lastIndexOf('.');
        return dot > 0 && EXTENSIONS.contains(name.substring(dot + 1));
    }

    private int naturalCompare(String a, String b) {
        String na = a.replaceAll("\\D", ""), nb = b.replaceAll("\\D", "");
        if (!na.isEmpty() && !nb.isEmpty() && na.length() < 10 && nb.length() < 10) {
            int c = Integer.compare(Integer.parseInt(na), Integer.parseInt(nb));
            if (c != 0) return c;
        }
        return a.compareTo(b);
    }

    private Map<String, String> loadMarker() {
        try {
            if (Files.exists(markerFile)) {
                return objectMapper.readValue(markerFile.toFile(), new TypeReference<LinkedHashMap<String, String>>() { });
            }
        } catch (Exception exception) {
            System.err.println("Could not read " + markerFile + ": " + exception.getMessage());
        }
        return new LinkedHashMap<>();
    }

    private void saveMarker(Map<String, String> done) {
        try {
            Files.createDirectories(markerFile.getParent());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(markerFile.toFile(), done);
        } catch (Exception exception) {
            System.err.println("Could not write " + markerFile + ": " + exception.getMessage());
        }
    }
}
