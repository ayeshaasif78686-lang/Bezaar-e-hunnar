package com.bazaarehunar.service;

import com.bazaarehunar.model.CatalogProduct;
import com.bazaarehunar.patterns.behavioral.iterator.ProductCatalog;
import com.bazaarehunar.patterns.behavioral.iterator.ProductIterator;
import com.bazaarehunar.patterns.creational.factorymethod.CustomizableProductCreator;
import com.bazaarehunar.patterns.creational.factorymethod.ProductCreator;
import com.bazaarehunar.patterns.creational.factorymethod.StandardProductCreator;
import com.bazaarehunar.patterns.structural.composite.CategoryComposite;
import com.bazaarehunar.patterns.structural.composite.CategoryLeaf;
import com.bazaarehunar.patterns.structural.flyweight.CraftStyleFactory;
import com.bazaarehunar.patterns.structural.flyweight.CraftStyleFlyweight;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class CatalogService {
    private final CraftStyleFactory styleFactory = new CraftStyleFactory();
    private final ProductCreator standardCreator = new StandardProductCreator();
    private final ProductCreator customizableCreator = new CustomizableProductCreator();
    private final List<CatalogProduct> products = new CopyOnWriteArrayList<>();
    private final CategoryComposite categoryTree;

    public CatalogService() {
        categoryTree = buildCategoryTree();
        seedProducts();
    }

    public List<CatalogProduct> search(String q, String category) {
        ProductCatalog catalog = new ProductCatalog(products);
        ProductIterator iterator = catalog.iterator();
        List<CatalogProduct> result = new ArrayList<>();
        String needle = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        while (iterator.hasNext()) {
            CatalogProduct product = iterator.next();
            boolean queryMatch = needle.isBlank()
                    || product.getName().toLowerCase(Locale.ROOT).contains(needle)
                    || product.getCategory().toLowerCase(Locale.ROOT).contains(needle)
                    || product.getArtisan().toLowerCase(Locale.ROOT).contains(needle)
                    || product.getRegion().toLowerCase(Locale.ROOT).contains(needle)
                    || product.getMaterial().toLowerCase(Locale.ROOT).contains(needle);
            boolean categoryMatch = category == null || category.isBlank() || product.getCategory().equalsIgnoreCase(category);
            if (queryMatch && categoryMatch) result.add(product);
        }
        return result;
    }

    public Optional<CatalogProduct> findById(String id) {
        return products.stream().filter(product -> product.getId().equals(id)).findFirst();
    }

    public CatalogProduct create(CatalogProduct input) {
        ProductCreator creator = input.isCustomizable() ? customizableCreator : standardCreator;
        CraftStyleFlyweight style = styleFactory.getStyle(
                value(input.getRegion(), "Pakistan"), value(input.getMaterial(), "Mixed"), "Handmade"
        );
        CatalogProduct created = creator.create(
                UUID.randomUUID().toString(), input.getName(), input.getCategory(), input.getPrice(),
                input.getRating(), input.getReviews(), input.getBadge(), input.getImage(), input.getArtisan(),
                input.getArtisanId(), input.getDescription(), style
        );
        products.add(created);
        return created;
    }

    public CatalogProduct update(String id, CatalogProduct input) {
        CatalogProduct existing = findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        existing.setName(input.getName());
        existing.setCategory(input.getCategory());
        existing.setPrice(input.getPrice());
        existing.setRating(input.getRating());
        existing.setReviews(input.getReviews());
        existing.setBadge(input.getBadge());
        existing.setImage(input.getImage());
        existing.setArtisan(input.getArtisan());
        existing.setArtisanId(input.getArtisanId());
        existing.setRegion(input.getRegion());
        existing.setMaterial(input.getMaterial());
        existing.setDescription(input.getDescription());
        existing.setCustomizable(input.isCustomizable());
        CraftStyleFlyweight style = styleFactory.getStyle(value(input.getRegion(), "Pakistan"), value(input.getMaterial(), "Mixed"), "Handmade");
        existing.setStyleKey(style.key());
        return existing;
    }

    public void delete(String id) {
        products.removeIf(product -> product.getId().equals(id));
    }

    public CategoryComposite getCategoryTree() { return categoryTree; }
    public int flyweightCount() { return styleFactory.cachedStyleCount(); }
    public int productCount() { return products.size(); }

    private CategoryComposite buildCategoryTree() {
        return new CategoryComposite("All Categories")
                .add(new CategoryLeaf("Ajrak Shawl"))
                .add(new CategoryLeaf("Carved Wooden Decorative Bowl"))
                .add(new CategoryLeaf("Handcrafted Wooden Serving Tray"))
                .add(new CategoryLeaf("Handmade Ajrak Tote Bag"))
                .add(new CategoryLeaf("kashmiri Bags"))
                .add(new CategoryLeaf("khusa"))
                .add(new CategoryLeaf("Phulkari Dupatta"))
                .add(new CategoryLeaf("Sindhi Topi"))
                .add(new CategoryLeaf("Traditional Brass Candle Holder"));
    }

    private void seedProducts() {
        addSeedVariants(standardCreator, "ajrak-shawl", "Ajrak Shawl", "Ajrak Shawl", 3900, 4.8, 32, "Bestseller", "/assets/products/ajrak-shawl/", extensionList("jpg,jpg,jpg,jpg,png"), "Zainab Bibi", "zainab-bibi", "Sindh", "Cotton Fabric", "Block Printing");
        addSeedVariants(customizableCreator, "carved-wooden-decorative-bowl", "Carved Wooden Decorative Bowl", "Carved Wooden Decorative Bowl", 3200, 4.9, 26, "New", "/assets/products/carved-wooden-decorative-bowl/", extensionList("jpg,png,png,png,png,png,png,png,png,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg"), "Rashid Ali", "rashid-ali", "Lahore", "Wood", "Wood Carving");
        addSeedVariants(standardCreator, "handcrafted-wooden-serving-tray", "Handcrafted Wooden Serving Tray", "Handcrafted Wooden Serving Tray", 2800, 4.7, 24, "", "/assets/products/handcrafted-wooden-serving-tray/", extensionList("png,png,jpg,jpg,jpg,jpg,png,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,png,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,png"), "Rashid Ali", "rashid-ali", "Lahore", "Wood", "Wood Craft");
        addSeedVariants(customizableCreator, "handmade-ajrak-tote-bag", "Handmade Ajrak Tote Bag", "Handmade Ajrak Tote Bag", 2700, 4.6, 21, "New", "/assets/products/handmade-ajrak-tote-bag/", extensionList("png,png,png,png,png,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg"), "Zainab Bibi", "zainab-bibi", "Sindh", "Cotton Fabric", "Textile Craft");
        addSeedVariants(standardCreator, "kashmiri-bags", "Kashmiri Bag", "kashmiri Bags", 3500, 4.8, 18, "", "/assets/products/kashmiri-bags/", extensionList("png,jpg,jpg,jpg,jpg,jpg"), "Ayesha Noor", "ayesha-noor", "Kashmir", "Fabric", "Embroidery");
        addSeedVariants(standardCreator, "phulkari-dupatta", "Phulkari Dupatta", "Phulkari Dupatta", 4200, 4.9, 34, "Bestseller", "/assets/products/phulkari-dupatta/", extensionList("png,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg"), "Imran Khan", "imran-khan", "Punjab", "Embroidered Fabric", "Phulkari Embroidery");
        addSeedVariants(standardCreator, "sindhi-topi", "Sindhi Topi", "Sindhi Topi", 1800, 4.7, 20, "", "/assets/products/sindhi-topi/", extensionList("jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg"), "Ustad Karim", "ustad-karim", "Sindh", "Fabric", "Hand Stitching");
        addSeedVariants(standardCreator, "traditional-brass-candle-holder", "Traditional Brass Candle Holder", "Traditional Brass Candle Holder", 2600, 4.8, 16, "New", "/assets/products/traditional-brass-candle-holder/", extensionList("png,png,png,png,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,png,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg,jpg"), "Ustad Karim", "ustad-karim", "Multan", "Brass", "Metal Craft");
    }

    private void addSeedVariants(ProductCreator creator, String baseId, String baseName, String category, int basePrice,
                                 double rating, int reviews, String firstBadge, String imageFolder, String[] extensions,
                                 String artisan, String artisanId, String region, String material, String technique) {
        for (int index = 0; index < extensions.length; index++) {
            String badge = index == 0 ? firstBadge : (index % 5 == 0 ? "Limited" : "");
            String productName = index == 0 ? baseName : baseName + " Edition " + (index + 1);
            addSeed(creator, baseId + "-" + (index + 1), productName, category,
                    basePrice + index * 250, Math.min(5.0, rating + index * 0.05), reviews + index * 3, badge,
                    imageFolder + (index + 1) + "." + extensions[index], artisan, artisanId, region, material, technique);
        }
    }

    private String[] extensionList(String values) {
        return values.split(",");
    }

    private void addSeed(ProductCreator creator, String id, String name, String category, int price, double rating,
                         int reviews, String badge, String image, String artisan, String artisanId,
                         String region, String material, String technique) {
        CraftStyleFlyweight style = styleFactory.getStyle(region, material, technique);
        products.add(creator.create(id, name, category, price, rating, reviews, badge, image, artisan, artisanId,
                "Authentic handmade " + name.toLowerCase(Locale.ROOT) + " created by skilled artisans.", style));
    }

    private String value(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
