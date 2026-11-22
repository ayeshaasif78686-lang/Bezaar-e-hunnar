package com.bazaarehunar.controller;

import com.bazaarehunar.patterns.showcase.PatternCatalog;
import com.bazaarehunar.patterns.showcase.PatternDemos;
import com.bazaarehunar.patterns.showcase.PatternInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Public showcase endpoints for the 23 design patterns (all under /api/patterns, which SecurityConfig
 * already permits). The older GET /api/patterns/cart-demo endpoint in CartPatternController is unchanged.
 */
@RestController
@RequestMapping("/api/patterns")
public class PatternShowcaseController {

    /** GET /api/patterns : the full list of the 23 patterns with their category, package and purpose. */
    @GetMapping
    public Map<String, Object> list() {
        List<PatternInfo> all = PatternCatalog.all();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("total", all.size());
        body.put("creational", PatternCatalog.count("Creational"));
        body.put("structural", PatternCatalog.count("Structural"));
        body.put("behavioral", PatternCatalog.count("Behavioral"));
        body.put("patterns", all);
        return body;
    }

    /** GET /api/patterns/demo : runs the demo of every pattern. */
    @GetMapping("/demo")
    public Map<String, Object> demoAll() {
        return PatternDemos.runAll();
    }

    /** GET /api/patterns/demo/{key} : runs one pattern demo, e.g. /api/patterns/demo/observer. */
    @GetMapping("/demo/{key}")
    public Map<String, Object> demo(@PathVariable String key) {
        if (!PatternDemos.exists(key)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown pattern: " + key);
        }
        return PatternDemos.run(key);
    }
}
