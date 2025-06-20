package com.cabrooster.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "passengers")
public class Passenger extends User {
    private Double currentLatitude;
    private Double currentLongitude;
    private Double homeLatitude;
    private Double homeLongitude;
    private boolean needsRide = false;

    // Getters and Setters
    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public Double getHomeLatitude() {
        return homeLatitude;
    }

    public void setHomeLatitude(Double homeLatitude) {
        this.homeLatitude = homeLatitude;
    }

    public Double getHomeLongitude() {
        return homeLongitude;
    }

    public void setHomeLongitude(Double homeLongitude) {
        this.homeLongitude = homeLongitude;
    }

    public boolean isNeedsRide() {
        return needsRide;
    }

    public void setNeedsRide(boolean needsRide) {
        this.needsRide = needsRide;
    }
}
