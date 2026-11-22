package com.bazaarehunar.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashMap;
import java.util.Map;

/** Single settings document (collection "settings", id "store"). */
@Document(collection = "settings")
public class StoreSettings {
    @Id
    private String id = "store";
    private Map<String, Object> values = new LinkedHashMap<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Map<String, Object> getValues() { return values; }
    public void setValues(Map<String, Object> values) { this.values = values; }
}
