package com.cabrooster.service;

import com.cabrooster.model.CabDriver;
import com.cabrooster.model.Passenger;
import com.cabrooster.model.RideAssignment;
import com.cabrooster.repository.CabDriverRepository;
import com.cabrooster.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.cabrooster.repository.RideAssignmentRepository;

@Service
public class RideAssignmentService {

    private static final double MAX_DISTANCE_METERS = 100; // 100 meters
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    @Autowired
    private RideAssignmentRepository rideAssignmentRepository;
    
    @Autowired
    private CabDriverRepository cabDriverRepository;
    
    @Autowired
    private PassengerRepository passengerRepository;
    
    @Autowired
    private LocationService locationService;

    /**
     * Schedule ride assignments at 8:00 AM and 7:00 PM daily
     */
    @Scheduled(cron = "0 0 8,19 * * ?") // 8 AM and 7 PM every day
    @Transactional
    public void scheduleRideAssignments() {
        // Find all available cabs
        List<CabDriver> availableCabs = cabDriverRepository.findByAvailable(true);
        
        // Find all passengers who need a ride
        List<Passenger> passengersNeedingRide = passengerRepository.findByNeedsRide(true);
        
        // For each passenger, find the nearest available cab
        for (Passenger passenger : passengersNeedingRide) {
            Optional<CabDriver> nearestCab = findNearestAvailableCab(
                    passenger.getCurrentLatitude(), 
                    passenger.getCurrentLongitude(),
                    availableCabs);
            
            if (nearestCab.isPresent()) {
                assignRide(nearestCab.get(), passenger);
                // Remove the assigned cab from available cabs
                availableCabs.remove(nearestCab.get());
            }
        }
    }
    
    /**
     * Find the nearest available cab to the given location
     */
    private Optional<CabDriver> findNearestAvailableCab(Double latitude, Double longitude, 
                                                       List<CabDriver> availableCabs) {
        if (availableCabs.isEmpty()) {
            return Optional.empty();
        }
        
        CabDriver nearestCab = null;
        double minDistance = Double.MAX_VALUE;
        
        for (CabDriver cab : availableCabs) {
            double distance = locationService.calculateDistance(
                    latitude, longitude,
                    cab.getCurrentLatitude(), cab.getCurrentLongitude());
                    
            if (distance < minDistance && distance <= MAX_DISTANCE_METERS) {
                minDistance = distance;
                nearestCab = cab;
            }
        }
        
        return Optional.ofNullable(nearestCab);
    }
    
    /**
     * Assign a ride to a cab driver
     */
    @Transactional
    public RideAssignment assignRide(CabDriver cabDriver, Passenger passenger) {
        // Mark cab as unavailable
        cabDriver.setAvailable(false);
        cabDriverRepository.save(cabDriver);
        
        // Mark passenger as assigned
        passenger.setNeedsRide(false);
        passengerRepository.save(passenger);
        
        // Create and save ride assignment
        RideAssignment assignment = new RideAssignment();
        assignment.setCabDriver(cabDriver);
        assignment.setPassenger(passenger);
        assignment.setAssignmentTime(LocalDateTime.now());
        assignment.setStatus(STATUS_PENDING);
        
        return rideAssignmentRepository.save(assignment);
    }
    
    /**
     * Update ride status
     */
    @Transactional
    public RideAssignment updateRideStatus(Long assignmentId, String status) {
        return rideAssignmentRepository.findById(assignmentId).map(assignment -> {
            assignment.setStatus(status);
            
            if (STATUS_COMPLETED.equals(status) || STATUS_CANCELLED.equals(status)) {
                // Mark cab as available again
                CabDriver cabDriver = assignment.getCabDriver();
                cabDriver.setAvailable(true);
                cabDriverRepository.save(cabDriver);
                
                // Update completion time
                assignment.setCompletionTime(LocalDateTime.now());
            }
            
            return rideAssignmentRepository.save(assignment);
        }).orElseThrow(() -> new RuntimeException("Ride assignment not found"));
    }
    
    /**
     * Get all ride assignments
     */
    public List<RideAssignment> getAllRideAssignments() {
        return rideAssignmentRepository.findAll();
    }

    /**
     * Get ride assignment by ID
     */
    public Optional<RideAssignment> getRideAssignmentById(Long id) {
        return rideAssignmentRepository.findById(id);
    }

    /**
     * Get active rides for a cab driver
     */
    public List<RideAssignment> getActiveRidesForCab(Long cabDriverId) {
        return rideAssignmentRepository.findByCabDriverIdAndStatus(cabDriverId, STATUS_IN_PROGRESS);
    }

    /**
     * Get ride history for a passenger
     */
    public List<RideAssignment> getRideHistoryForPassenger(Long passengerId) {
        return rideAssignmentRepository.findByPassengerIdAndStatusIn(
            passengerId, 
            List.of(STATUS_COMPLETED, STATUS_CANCELLED)
        );
    }

}
