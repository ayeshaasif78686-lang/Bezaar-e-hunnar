package com.bazaarehunar.controller;

import com.bazaarehunar.model.CatalogProduct;
import com.bazaarehunar.patterns.structural.composite.CategoryComponent;
import com.bazaarehunar.service.CatalogService;
import com.bazaarehunar.service.CloudinaryUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CatalogController {
    private final CatalogService catalogService;
    private final CloudinaryUploadService cloudinaryUploadService;
    public CatalogController(CatalogService catalogService, CloudinaryUploadService cloudinaryUploadService) {
        this.catalogService = catalogService;
        this.cloudinaryUploadService = cloudinaryUploadService;
    }

    @GetMapping("/products")
    public List<CatalogProduct> products(@RequestParam(required = false) String q,
                                         @RequestParam(required = false) String category,
                                         @RequestParam(defaultValue = "false") boolean includeInactive,
                                         Authentication authentication) {
        boolean admin = authentication != null
                && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return catalogService.search(q, category, includeInactive && admin);
    }

    /** Interpreter pattern: GET /api/products/advanced-search?q=category:pottery AND price<5000 */
    @GetMapping("/products/advanced-search")
    public List<CatalogProduct> advancedSearch(@RequestParam String q) {
        return catalogService.advancedSearch(q);
    }

    /** Template Method pattern: GET /api/products/report?format=csv|text|featured */
    @GetMapping(value = "/products/report", produces = "text/plain;charset=UTF-8")
    public String report(@RequestParam(defaultValue = "csv") String format) {
        return catalogService.report(format);
    }

    @GetMapping("/products/{id}")
    public CatalogProduct product(@PathVariable String id) {
        return catalogService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @PostMapping("/products")
    public CatalogProduct create(@RequestBody CatalogProduct input, Authentication authentication) {
        requireSellerOrAdmin(authentication);
        return catalogService.create(input);
    }

    /** Prototype pattern: duplicate a listing under a new name. Body: {"name": "New name"} */
    @PostMapping("/products/{id}/clone")
    public CatalogProduct cloneProduct(@PathVariable String id, @RequestBody Map<String, String> body,
                                       Authentication authentication) {
        requireSellerOrAdmin(authentication);
        return catalogService.cloneProduct(id, body.get("name"));
    }

    @PutMapping("/products/{id}")
    public CatalogProduct update(@PathVariable String id, @RequestBody CatalogProduct input, Authentication authentication) {
        requireSellerOrAdmin(authentication);
        return catalogService.update(id, input);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id, Authentication authentication) {
        requireSellerOrAdmin(authentication);
        catalogService.delete(id);
    }

    @PostMapping(value = "/products/{id}/images", consumes = "multipart/form-data")
    public Map<String, String> uploadProductImage(@PathVariable String id, @RequestParam("image") MultipartFile image,
                                                   Authentication authentication) {
        requireSellerOrAdmin(authentication);
        CatalogProduct product = catalogService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return cloudinaryUploadService.uploadProductImage(image, id, product.getCategory());
    }

    @GetMapping("/categories")
    public List<Map<String, Object>> publicCategories() { return catalogService.categories(); }

    @GetMapping("/categories/tree")
    public Map<String, Object> categories() {
        return componentToMap(catalogService.getCategoryTree());
    }

    @GetMapping("/patterns/sprint2-demo")
    public Map<String, Object> patternDemo() {
        return Map.of(
                "factoryMethod", "StandardProductCreator and CustomizableProductCreator create catalog products",
                "composite", "Category tree is represented by CategoryComposite and CategoryLeaf",
                "flyweight", "Repeated region/material/technique metadata is shared; cached styles=" + catalogService.flyweightCount(),
                "iterator", "CatalogService search traverses ProductCatalog through ProductIterator",
                "products", catalogService.productCount()
        );
    }

    private Map<String, Object> componentToMap(CategoryComponent component) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("name", component.name());
        node.put("leaf", component.leaf());
        node.put("children", component.children().stream().map(this::componentToMap).toList());
        return node;
    }

    private void requireSellerOrAdmin(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(a ->
                a.getAuthority().equals("ROLE_SELLER") || a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller or Admin role required");
        }
    }

    private void findProductOrThrow(String id) {
        if (catalogService.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }
}
