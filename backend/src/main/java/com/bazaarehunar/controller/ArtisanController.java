package com.bazaarehunar.controller;

import com.bazaarehunar.model.Artisan;
import com.bazaarehunar.service.ArtisanService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/artisans")
public class ArtisanController {
    private final ArtisanService artisanService;

    public ArtisanController(ArtisanService artisanService) {
        this.artisanService = artisanService;
    }

    @GetMapping
    public List<Artisan> list() {
        return artisanService.findAll();
    }

    @GetMapping("/{id}")
    public Artisan find(@PathVariable String id) {
        return artisanService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artisan not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Artisan create(@RequestBody Artisan input, Authentication authentication) {
        requireSellerOrAdmin(authentication);
        return artisanService.create(input);
    }

    @PutMapping("/{id}")
    public Artisan update(@PathVariable String id, @RequestBody Artisan input, Authentication authentication) {
        requireSellerOrAdmin(authentication);
        return artisanService.update(id, input);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id, Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
        }
        artisanService.delete(id);
    }

    private void requireSellerOrAdmin(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(a ->
                a.getAuthority().equals("ROLE_SELLER") || a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller or Admin role required");
        }
    }
}
