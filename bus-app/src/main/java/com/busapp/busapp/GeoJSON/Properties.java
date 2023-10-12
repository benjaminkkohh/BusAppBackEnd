package com.busapp.busapp.GeoJSON;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class Properties {
    private String VehicleRef;
    private String PublishedLineName;
    private String DirectionRef;
    private String OriginName;
    private String DestinationName;
    private String StartTime;
    private String EndTime;
    private String NumOfPoints = "1";
    private Map<String, Object> points = new LinkedHashMap<>();

    public String getVehicleRef() {
        return this.VehicleRef;
    }

    public void setVehicleRef(String VehicleRef) {
        this.VehicleRef = VehicleRef;
    }

    public String getPublishedLineName() {
        return this.PublishedLineName;
    }

    public void setPublishedLineName(String PublishedLineName) {
        this.PublishedLineName = PublishedLineName;
    }

    public String getDirectionRef() {
        return this.DirectionRef;
    }

    public void setDirectionRef(String DirectionRef) {
        this.DirectionRef = DirectionRef;
    }

    public String getOriginName() {
        return this.OriginName;
    }

    public void setOriginName(String OriginName) {
        this.OriginName = OriginName;
    }

    public String getDestinationName() {
        return this.DestinationName;
    }

    public void setDestinationName(String DestinationName) {
        this.DestinationName = DestinationName;
    }

    public String getStartTime() {
        return this.StartTime;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }

    public String getEndTime() {
        return this.EndTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    public String getNumOfPoints() {
        return this.NumOfPoints;
    }

    public void setNumOfPoints(String NumOfPoints) {
        this.NumOfPoints = NumOfPoints;
    }
    @JsonAnyGetter
    // Getter and setter for points
    public Map<String, Object> getPoints() {
        return points;
    }
    @JsonAnySetter
    // Method to add a point dynamically
    public void setPoint(Map<String, Object> point) {
        points.putAll(point);
    }
}
