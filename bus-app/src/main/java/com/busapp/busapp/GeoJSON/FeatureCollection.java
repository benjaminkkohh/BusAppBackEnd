package com.busapp.busapp.GeoJSON;

import java.util.List;

public class FeatureCollection {
    private String type;
    private List<Feature> features;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Feature> getFeatures() {
        return this.features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    // Getters and setters
}