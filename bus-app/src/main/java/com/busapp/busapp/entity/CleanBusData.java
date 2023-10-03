package com.busapp.busapp.entity;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "clean_bus_data")
public class CleanBusData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recorded_at_time")
    private Date recordedAtTime; // Change data type to Date

    @Column(name = "direction_ref")
    private int directionRef; 

    @Column(name = "published_line_name")
    private String publishedLineName;

    @Column(name = "origin_name")
    private String originName;

    @Column(name = "origin_lat")
    private Double originLat; // Change data type to Double

    @Column(name = "origin_long")
    private Double originLong; // Change data type to Double

    @Column(name = "destination_name")
    private String destinationName;

    @Column(name = "destination_lat")
    private Double destinationLat; // Change data type to Double

    @Column(name = "destination_long")
    private Double destinationLong; // Change data type to Double

    @Column(name = "vehicle_ref")
    private String vehicleRef;

    @Column(name = "vehicle_location_latitude")
    private Double vehicleLocationLatitude; // Change data type to Double

    @Column(name = "vehicle_location_longitude")
    private Double vehicleLocationLongitude; // Change data type to Double

    @Column(name = "next_stop_point_name")
    private String nextStopPointName;

    @Column(name = "arrival_proximity_text")
    private String arrivalProximityText;

    @Column(name = "distance_from_stop")
    private Integer distanceFromStop; // Change data type to Integer

    @Column(name = "expected_arrival_time")
    private Date expectedArrivalTime; // Change data type to Date

    @Column(name = "scheduled_arrival_time")
    private LocalTime scheduledArrivalTime;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRecordedAtTime() {
        return this.recordedAtTime;
    }

    public void setRecordedAtTime(Date recordedAtTime) {
        this.recordedAtTime = recordedAtTime;
    }

    public int getDirectionRef() {
        return this.directionRef;
    }

    public void setDirectionRef(int directionRef) {
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

    public Double getOriginLat() {
        return this.originLat;
    }

    public void setOriginLat(Double originLat) {
        this.originLat = originLat;
    }

    public Double getOriginLong() {
        return this.originLong;
    }

    public void setOriginLong(Double originLong) {
        this.originLong = originLong;
    }

    public String getDestinationName() {
        return this.destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public Double getDestinationLat() {
        return this.destinationLat;
    }

    public void setDestinationLat(Double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public Double getDestinationLong() {
        return this.destinationLong;
    }

    public void setDestinationLong(Double destinationLong) {
        this.destinationLong = destinationLong;
    }

    public String getVehicleRef() {
        return this.vehicleRef;
    }

    public void setVehicleRef(String vehicleRef) {
        this.vehicleRef = vehicleRef;
    }

    public Double getVehicleLocationLatitude() {
        return this.vehicleLocationLatitude;
    }

    public void setVehicleLocationLatitude(Double vehicleLocationLatitude) {
        this.vehicleLocationLatitude = vehicleLocationLatitude;
    }

    public Double getVehicleLocationLongitude() {
        return this.vehicleLocationLongitude;
    }

    public void setVehicleLocationLongitude(Double vehicleLocationLongitude) {
        this.vehicleLocationLongitude = vehicleLocationLongitude;
    }

    public String getNextStopPointName() {
        return this.nextStopPointName;
    }

    public void setNextStopPointName(String nextStopPointName) {
        this.nextStopPointName = nextStopPointName;
    }

    public String getArrivalProximityText() {
        return this.arrivalProximityText;
    }

    public void setArrivalProximityText(String arrivalProximityText) {
        this.arrivalProximityText = arrivalProximityText;
    }

    public Integer getDistanceFromStop() {
        return this.distanceFromStop;
    }

    public void setDistanceFromStop(Integer distanceFromStop) {
        this.distanceFromStop = distanceFromStop;
    }

    public Date getExpectedArrivalTime() {
        return this.expectedArrivalTime;
    }

    public void setExpectedArrivalTime(Date expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
    }

    public LocalTime getScheduledArrivalTime() {
        return this.scheduledArrivalTime;
    }

    public void setScheduledArrivalTime(LocalTime scheduledArrivalTime) {
        this.scheduledArrivalTime = scheduledArrivalTime;
    }

    // Getters and setters for the fields
    // ...
}
