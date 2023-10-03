package com.busapp.busapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "raw_bus_data") 
public class RawBusData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recorded_at_time")
    private String recordedAtTime;

    @Column(name = "direction_ref")
    private String directionRef;

    @Column(name = "published_line_name")
    private String publishedLineName;

    @Column(name = "origin_name")
    private String originName;

    @Column(name = "origin_lat")
    private String originLat;

    @Column(name = "origin_long")
    private String originLong;

    @Column(name = "destination_name")
    private String destinationName;

    @Column(name = "destination_lat")
    private String destinationLat;

    @Column(name = "destination_long")
    private String destinationLong;

    @Column(name = "vehicle_ref")
    private String vehicleRef;

    @Column(name = "vehicle_location_latitude")
    private String vehicleLocationLatitude;

    @Column(name = "vehicle_location_longitude")
    private String vehicleLocationLongitude;

    @Column(name = "next_stop_point_name")
    private String nextStopPointName;

    @Column(name = "unknown")
    private String unknown;

    

    @Column(name = "arrival_proximity_text")
    private String arrivalProximityText;

    @Column(name = "distance_from_stop")
    private String distanceFromStop;

    @Column(name = "expected_arrival_time")
    private String expectedArrivalTime;

    @Column(name = "scheduled_arrival_time")
    private String scheduledArrivalTime;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecordedAtTime() {
        return this.recordedAtTime;
    }

    public void setRecordedAtTime(String recordedAtTime) {
        this.recordedAtTime = recordedAtTime;
    }

    public String getDirectionRef() {
        return this.directionRef;
    }

    public void setDirectionRef(String directionRef) {
        this.directionRef = directionRef;
    }

    public String getPublishedLineName() {
        return this.publishedLineName;
    }

    public void setPublishedLineName(String publishedLineName) {
        this.publishedLineName = publishedLineName;
    }

    public String getOriginName() {
        return this.originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getOriginLat() {
        return this.originLat;
    }

    public void setOriginLat(String originLat) {
        this.originLat = originLat;
    }

    public String getOriginLong() {
        return this.originLong;
    }

    public void setOriginLong(String originLong) {
        this.originLong = originLong;
    }

    public String getDestinationName() {
        return this.destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationLat() {
        return this.destinationLat;
    }

    public void setDestinationLat(String destinationLat) {
        this.destinationLat = destinationLat;
    }

    public String getDestinationLong() {
        return this.destinationLong;
    }

    public void setDestinationLong(String destinationLong) {
        this.destinationLong = destinationLong;
    }

    public String getVehicleRef() {
        return this.vehicleRef;
    }

    public void setVehicleRef(String vehicleRef) {
        this.vehicleRef = vehicleRef;
    }

    public String getVehicleLocationLatitude() {
        return this.vehicleLocationLatitude;
    }

    public void setVehicleLocationLatitude(String vehicleLocationLatitude) {
        this.vehicleLocationLatitude = vehicleLocationLatitude;
    }

    public String getVehicleLocationLongitude() {
        return this.vehicleLocationLongitude;
    }

    public void setVehicleLocationLongitude(String vehicleLocationLongitude) {
        this.vehicleLocationLongitude = vehicleLocationLongitude;
    }

    public String getNextStopPointName() {
        return this.nextStopPointName;
    }

    public void setNextStopPointName(String nextStopPointName) {
        this.nextStopPointName = nextStopPointName;
    }
    public String getUnknown() {
        return this.unknown;
    }

    public void setUnknown(String unknown) {
        this.unknown = unknown;
    }

    public String getArrivalProximityText() {
        return this.arrivalProximityText;
    }

    public void setArrivalProximityText(String arrivalProximityText) {
        this.arrivalProximityText = arrivalProximityText;
    }

    public String getDistanceFromStop() {
        return this.distanceFromStop;
    }

    public void setDistanceFromStop(String distanceFromStop) {
        this.distanceFromStop = distanceFromStop;
    }

    public String getExpectedArrivalTime() {
        return this.expectedArrivalTime;
    }

    public void setExpectedArrivalTime(String expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
    }

    public String getScheduledArrivalTime() {
        return this.scheduledArrivalTime;
    }

    public void setScheduledArrivalTime(String scheduledArrivalTime) {
        this.scheduledArrivalTime = scheduledArrivalTime;
    }

}


