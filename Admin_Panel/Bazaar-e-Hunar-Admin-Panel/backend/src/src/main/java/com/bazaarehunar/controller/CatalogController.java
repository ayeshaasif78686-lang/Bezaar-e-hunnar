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
                                         @RequestParam(required = false) String category) {
        return catalogService.search(q, category);
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
        findProductOrThrow(id);
        return cloudinaryUploadService.uploadProductImage(image, id);
    }

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
