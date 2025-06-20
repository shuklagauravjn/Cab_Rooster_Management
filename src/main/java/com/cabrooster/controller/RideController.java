package com.cabrooster.controller;

import com.cabrooster.model.RideAssignment;
import com.cabrooster.service.RideAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private RideAssignmentService rideAssignmentService;

    // Get all ride assignments
    @GetMapping
    public List<RideAssignment> getAllRideAssignments() {
        return rideAssignmentService.getAllRideAssignments();
    }

    // Get ride assignment by ID
    @GetMapping("/{id}")
    public ResponseEntity<RideAssignment> getRideAssignmentById(@PathVariable Long id) {
        return rideAssignmentService.getRideAssignmentById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Update ride status
    @PutMapping("/{id}/status")
    public ResponseEntity<RideAssignment> updateRideStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        try {
            return ResponseEntity.ok(rideAssignmentService.updateRideStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get active rides for a cab driver
    @GetMapping("/cab/{cabDriverId}/active")
    public List<RideAssignment> getActiveRidesForCab(@PathVariable Long cabDriverId) {
        return rideAssignmentService.getActiveRidesForCab(cabDriverId);
    }

    // Get ride history for a passenger
    @GetMapping("/passenger/{passengerId}/history")
    public List<RideAssignment> getRideHistoryForPassenger(@PathVariable Long passengerId) {
        return rideAssignmentService.getRideHistoryForPassenger(passengerId);
    }
}
