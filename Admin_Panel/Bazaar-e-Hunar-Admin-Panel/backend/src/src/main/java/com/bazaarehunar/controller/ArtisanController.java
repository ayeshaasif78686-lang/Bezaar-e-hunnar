package com.bazaarehunar.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/artisans")
public class ArtisanController {
    private final Map<String, Map<String, Object>> artisans = new LinkedHashMap<>();

    public ArtisanController() {
        artisans.put("ustad-karim", artisan("ustad-karim", "Ustad Karim", "Heritage Craft Artisan", "Multan, Pakistan", "10+", "250+", 4.9, 128,
                "Craft keeps our heritage alive for the next generation.",
                "Ustad Karim creates traditional brass and heritage craft pieces with careful handmade finishing.",
                "/assets/products/traditional-brass-candle-holder/1.png"));
        artisans.put("zainab-bibi", artisan("zainab-bibi", "Zainab Bibi", "Textile Artist", "Lahore, Pakistan", "8+", "180+", 4.8, 96,
                "Every stitch tells a story of our heritage.", "Zainab creates textile pieces inspired by regional embroidery and weaving traditions.",
                "/assets/products/ajrak-shawl/1.jpg"));
        artisans.put("rashid-ali", artisan("rashid-ali", "Rashid Ali", "Wood Craft Specialist", "Lahore, Pakistan", "12+", "320+", 4.8, 105,
                "I turn wood into stories that last for generations.", "Rashid works with carved wood and handcrafted decorative forms.",
                "/assets/products/carved-wooden-decorative-bowl/1.jpg"));
    }

    @GetMapping("/{id}")
    public Map<String, Object> find(@PathVariable String id) {
        Map<String, Object> artisan = artisans.get(id);
        if (artisan == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artisan not found");
        return artisan;
    }

    private Map<String, Object> artisan(String id, String name, String role, String city, String years, String sold,
                                        double rating, int reviews, String quote, String bio, String image) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id); data.put("name", name); data.put("role", role); data.put("city", city);
        data.put("years", years); data.put("sold", sold); data.put("rating", rating); data.put("reviews", reviews);
        data.put("quote", quote); data.put("bio", bio); data.put("image", image);
        return data;
    }
}
