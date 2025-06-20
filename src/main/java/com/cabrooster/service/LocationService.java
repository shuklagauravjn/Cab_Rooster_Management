package com.cabrooster.service;

import org.springframework.stereotype.Service;

@Service
public class LocationService {
    
    // Earth's radius in kilometers
    private static final double EARTH_RADIUS = 6371.0;
    
    /**
     * Calculate distance between two points in meters using Haversine formula
     * @param lat1 Latitude of point 1
     * @param lon1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lon2 Longitude of point 2
     * @return Distance in meters
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        // Differences in coordinates
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        
        // Haversine formula
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                  Math.cos(lat1Rad) * Math.cos(lat2Rad) * 
                  Math.pow(Math.sin(dLon / 2), 2);
                  
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c * 1000; // Convert to meters
        
        return distance;
    }
    
    /**
     * Check if two points are within a certain distance (in meters)
     */
    public boolean isWithinDistance(double lat1, double lon1, double lat2, double lon2, double maxDistanceMeters) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        return distance <= maxDistanceMeters;
    }
}
