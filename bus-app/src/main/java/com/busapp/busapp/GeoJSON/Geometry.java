package com.busapp.busapp.GeoJSON;

import java.util.List;

public class Geometry {
    private String type;
    private List<?> coordinates;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<?> getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(List<?> coordinates) {
        this.coordinates = coordinates;
    }

    // Getters and setters
}