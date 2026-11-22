package com.bazaarehunar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Map;

@Service
public class CloudinaryUploadService {
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public CloudinaryUploadService(
            @Value("${app.cloudinary.cloud-name:}") String cloudName,
            @Value("${app.cloudinary.api-key:}") String apiKey,
            @Value("${app.cloudinary.api-secret:}") String apiSecret,
            ObjectMapper objectMapper) {
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.objectMapper = objectMapper;
    }

    public Map<String, String> uploadProductImage(MultipartFile file, String productId) {
        return uploadProductImage(file, productId, null);
    }

    /**
     * Uploads into bazaar-e-hunar/products/<category-slug>/ so every product category
     * gets its own subfolder in Cloudinary (e.g. bazaar-e-hunar/products/ajrak-shawl).
     */
    public Map<String, String> uploadProductImage(MultipartFile file, String productId, String category) {
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new IllegalStateException("Cloudinary is not configured. Add CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY and CLOUDINARY_API_SECRET.");
        }
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("An image file is required");
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equalsIgnoreCase("image/png") || contentType.equalsIgnoreCase("image/jpeg")
                || contentType.equalsIgnoreCase("image/jpg") || contentType.equalsIgnoreCase("image/webp"))) {
            throw new IllegalArgumentException("Only PNG, JPG or WEBP images can be uploaded");
        }
        if (file.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException("Image must be 5 MB or smaller");
        }

        try {
            long timestamp = Instant.now().getEpochSecond();
            String folder = "bazaar-e-hunar/products/" + slug(category, productId);
            String publicId = sanitize(productId) + "-" + timestamp;
            String signature = sha1("folder=" + folder + "&public_id=" + publicId + "&timestamp=" + timestamp + apiSecret);

            ByteArrayResource imageResource = new ByteArrayResource(file.getBytes()) {
                @Override public String getFilename() { return file.getOriginalFilename(); }
            };
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", imageResource);
            body.add("api_key", apiKey);
            body.add("timestamp", String.valueOf(timestamp));
            body.add("folder", folder);
            body.add("public_id", publicId);
            body.add("signature", signature);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            String endpoint = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";
            String response = restTemplate.postForObject(endpoint, new HttpEntity<>(body, headers), String.class);
            Map<String, Object> payload = objectMapper.readValue(response, new TypeReference<>() { });
            return Map.of(
                    "url", String.valueOf(payload.get("secure_url")),
                    "publicId", String.valueOf(payload.get("public_id")),
                    "provider", "Cloudinary"
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Cloudinary image upload failed: " + exception.getMessage(), exception);
        }
    }

    public boolean isConfigured() {
        return !(cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank());
    }

    /** Uploads raw image bytes into an explicit Cloudinary folder (used by the asset sync). Returns the secure URL. */
    public String uploadBytes(byte[] data, String filename, String folder, String publicId) {
        if (!isConfigured()) throw new IllegalStateException("Cloudinary is not configured");
        try {
            long timestamp = Instant.now().getEpochSecond();
            String signature = sha1("folder=" + folder + "&public_id=" + publicId + "&timestamp=" + timestamp + apiSecret);
            ByteArrayResource resource = new ByteArrayResource(data) {
                @Override public String getFilename() { return filename; }
            };
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);
            body.add("api_key", apiKey);
            body.add("timestamp", String.valueOf(timestamp));
            body.add("folder", folder);
            body.add("public_id", publicId);
            body.add("signature", signature);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            String endpoint = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";
            String response = restTemplate.postForObject(endpoint, new HttpEntity<>(body, headers), String.class);
            Map<String, Object> payload = objectMapper.readValue(response, new TypeReference<>() { });
            return String.valueOf(payload.get("secure_url"));
        } catch (Exception exception) {
            throw new IllegalStateException("Cloudinary upload failed: " + exception.getMessage(), exception);
        }
    }

    /** Creates bazaar-e-hunar/products/<category-slug> in Cloudinary (no image needed). Safe to call repeatedly. */
    public boolean ensureCategoryFolder(String category) {
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) return false;
        try {
            String path = "bazaar-e-hunar/products/" + slug(category, null);
            HttpHeaders headers = new HttpHeaders();
            String auth = java.util.Base64.getEncoder().encodeToString((apiKey + ":" + apiSecret).getBytes(StandardCharsets.UTF_8));
            headers.set("Authorization", "Basic " + auth);
            String endpoint = "https://api.cloudinary.com/v1_1/" + cloudName + "/folders/" + path;
            restTemplate.postForObject(endpoint, new HttpEntity<>(headers), String.class);
            return true;
        } catch (Exception exception) {
            // Folder may already exist or the account may reject it; uploads still create it on demand.
            System.err.println("Cloudinary folder '" + category + "' not created: " + exception.getMessage());
            return false;
        }
    }

    private String slug(String category, String productId) {
        String source = category == null || category.isBlank() ? productId : category;
        String value = (source == null ? "" : source).trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
        return value.isBlank() ? "uncategorized" : value;
    }

    private String sanitize(String value) {
        return (value == null || value.isBlank() ? "product" : value).toLowerCase().replaceAll("[^a-z0-9_-]", "-");
    }

    private String sha1(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte item : bytes) hex.append(String.format("%02x", item));
        return hex.toString();
    }
}
