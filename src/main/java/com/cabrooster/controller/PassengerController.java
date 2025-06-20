package com.cabrooster.controller;

import com.cabrooster.model.Passenger;
import com.cabrooster.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    @Autowired
    private PassengerRepository passengerRepository;

    // Update passenger location (to be called every 15 minutes)
    @PutMapping("/{id}/location")
    public ResponseEntity<Passenger> updateLocation(
            @PathVariable Long id,
            @RequestParam double latitude,
            @RequestParam double longitude) {
        
        return passengerRepository.findById(id)
            .map(passenger -> {
                passenger.setCurrentLatitude(latitude);
                passenger.setCurrentLongitude(longitude);
                return ResponseEntity.ok(passengerRepository.save(passenger));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // Request a ride
    @PostMapping("/{id}/request-ride")
    public ResponseEntity<Passenger> requestRide(
            @PathVariable Long id,
            @RequestParam double currentLatitude,
            @RequestParam double currentLongitude) {
        
        return passengerRepository.findById(id)
            .map(passenger -> {
                passenger.setCurrentLatitude(currentLatitude);
                passenger.setCurrentLongitude(currentLongitude);
                passenger.setNeedsRide(true);
                return ResponseEntity.ok(passengerRepository.save(passenger));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // Get all passengers
    @GetMapping
    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    // Get passenger by ID
    @GetMapping("/{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable Long id) {
        return passengerRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Create a new passenger
    @PostMapping
    public Passenger createPassenger(@RequestBody Passenger passenger) {
        return passengerRepository.save(passenger);
    }

    // Update passenger details
    @PutMapping("/{id}")
    public ResponseEntity<Passenger> updatePassenger(
            @PathVariable Long id,
            @RequestBody Passenger passengerDetails) {
        
        return passengerRepository.findById(id)
            .map(passenger -> {
                passenger.setName(passengerDetails.getName());
                passenger.setEmail(passengerDetails.getEmail());
                passenger.setPhone(passengerDetails.getPhone());
                passenger.setHomeLatitude(passengerDetails.getHomeLatitude());
                passenger.setHomeLongitude(passengerDetails.getHomeLongitude());
                return ResponseEntity.ok(passengerRepository.save(passenger));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // Delete a passenger
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePassenger(@PathVariable Long id) {
        return passengerRepository.findById(id)
            .map(passenger -> {
                passengerRepository.delete(passenger);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
