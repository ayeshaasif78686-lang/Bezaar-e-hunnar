package com.bazaarehunar.service;

import com.bazaarehunar.model.Artisan;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** Artisan profiles stored in MongoDB (collection "artisans"). */
@Service
public class ArtisanService {
    private final MongoTemplate mongoTemplate;

    public ArtisanService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Artisan> findAll() {
        return mongoTemplate.find(new Query().with(Sort.by("name")), Artisan.class);
    }

    public Optional<Artisan> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, Artisan.class));
    }

    public Artisan create(Artisan input) {
        if (input.getName() == null || input.getName().isBlank()) {
            throw new IllegalArgumentException("Artisan name is required");
        }
        if (input.getId() == null || input.getId().isBlank()) {
            input.setId(slug(input.getName()));
        }
        if (mongoTemplate.exists(new Query(Criteria.where("_id").is(input.getId())), Artisan.class)) {
            throw new IllegalArgumentException("An artisan with this id already exists");
        }
        return mongoTemplate.insert(input);
    }

    public Artisan update(String id, Artisan input) {
        Artisan existing = findById(id).orElseThrow(() -> new IllegalArgumentException("Artisan not found"));
        existing.setName(input.getName() == null || input.getName().isBlank() ? existing.getName() : input.getName());
        existing.setRole(input.getRole());
        existing.setCity(input.getCity());
        existing.setYears(input.getYears());
        existing.setSold(input.getSold());
        existing.setRating(input.getRating());
        existing.setReviews(input.getReviews());
        existing.setQuote(input.getQuote());
        existing.setBio(input.getBio());
        existing.setImage(input.getImage());
        return mongoTemplate.save(existing);
    }

    public void delete(String id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), Artisan.class);
    }

    /**
     * Inserts the built-in artisans. The original five are added when the collection is empty; the artisans of the
     * newer categories are added once, when none of them exist yet. Existing artisans are never touched.
     */
    public void seedIfEmpty() {
        if (mongoTemplate.count(new Query(), Artisan.class) == 0) {
            mongoTemplate.insert(List.of(
                    new Artisan("ustad-karim", "Ustad Karim", "Heritage Craft Artisan", "Multan, Pakistan", "10+", "250+", 4.9, 128,
                            "Craft keeps our heritage alive for the next generation.",
                            "Ustad Karim creates traditional brass and heritage craft pieces with careful handmade finishing.",
                            "/assets/products/traditional-brass-candle-holder/1.png"),
                    new Artisan("zainab-bibi", "Zainab Bibi", "Textile Artist", "Lahore, Pakistan", "8+", "180+", 4.8, 96,
                            "Every stitch tells a story of our heritage.",
                            "Zainab creates textile pieces inspired by regional embroidery and weaving traditions.",
                            "/assets/products/ajrak-shawl/1.jpg"),
                    new Artisan("rashid-ali", "Rashid Ali", "Wood Craft Specialist", "Lahore, Pakistan", "12+", "320+", 4.8, 105,
                            "I turn wood into stories that last for generations.",
                            "Rashid works with carved wood and handcrafted decorative forms.",
                            "/assets/products/carved-wooden-decorative-bowl/1.jpg"),
                    // These two artisans are already used by catalog products, so they get a profile too.
                    new Artisan("ayesha-noor", "Ayesha Noor", "Embroidery Artist", "Srinagar, Kashmir", "9+", "140+", 4.8, 72,
                            "Every thread carries the colours of our valleys.",
                            "Ayesha creates hand-embroidered bags inspired by traditional Kashmiri patterns.",
                            "/assets/products/kashmiri-bags/1.png"),
                    new Artisan("imran-khan", "Imran Khan", "Phulkari Craftsman", "Punjab, Pakistan", "11+", "210+", 4.9, 88,
                            "Phulkari is the garden we stitch by hand.",
                            "Imran works on Phulkari dupattas using traditional Punjabi embroidery techniques.",
                            "/assets/products/phulkari-dupatta/1.png")
            ), Artisan.class);
        }
        seedNewCategoryArtisans();
    }

    /** Artisans of the newer categories, loaded from resources/seed/supplied-category-artisans.json (same as the website). */
    private void seedNewCategoryArtisans() {
        try (java.io.InputStream in = getClass().getResourceAsStream("/seed/supplied-category-artisans.json")) {
            if (in == null) return;
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper()
                    .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            List<Artisan> batch = mapper.readValue(in, new com.fasterxml.jackson.core.type.TypeReference<List<Artisan>>() { });
            List<String> ids = batch.stream().map(Artisan::getId).toList();
            if (mongoTemplate.count(new Query(Criteria.where("_id").in(ids)), Artisan.class) == 0) {
                mongoTemplate.insert(batch, Artisan.class);
            }
        } catch (Exception exception) {
            System.err.println("Could not load supplied category artisans: " + exception.getMessage());
        }
    }

    private String slug(String name) {
        return name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
