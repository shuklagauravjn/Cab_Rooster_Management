package com.cabrooster.controller;

import com.cabrooster.model.CabDriver;
import com.cabrooster.repository.CabDriverRepository;
import com.cabrooster.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cabs")
public class CabController {

    @Autowired
    private CabDriverRepository cabDriverRepository;
    
    @Autowired
    private LocationService locationService;

    // Update cab location (to be called every hour)
    @PutMapping("/{id}/location")
    public ResponseEntity<CabDriver> updateLocation(
            @PathVariable Long id,
            @RequestParam double latitude,
            @RequestParam double longitude) {
        
        return cabDriverRepository.findById(id)
            .map(cab -> {
                cab.setCurrentLatitude(latitude);
                cab.setCurrentLongitude(longitude);
                return ResponseEntity.ok(cabDriverRepository.save(cab));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // Get all cabs
    @GetMapping
    public List<CabDriver> getAllCabs() {
        return cabDriverRepository.findAll();
    }

    // Get cab by ID
    @GetMapping("/{id}")
    public ResponseEntity<CabDriver> getCabById(@PathVariable Long id) {
        return cabDriverRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Create a new cab
    @PostMapping
    public CabDriver createCab(@RequestBody CabDriver cabDriver) {
        return cabDriverRepository.save(cabDriver);
    }

    // Update cab details
    @PutMapping("/{id}")
    public ResponseEntity<CabDriver> updateCab(
            @PathVariable Long id,
            @RequestBody CabDriver cabDetails) {
        
        return cabDriverRepository.findById(id)
            .map(cab -> {
                cab.setName(cabDetails.getName());
                cab.setEmail(cabDetails.getEmail());
                cab.setPhone(cabDetails.getPhone());
                cab.setLicenseNumber(cabDetails.getLicenseNumber());
                cab.setCabNumber(cabDetails.getCabNumber());
                cab.setAvailable(cabDetails.isAvailable());
                return ResponseEntity.ok(cabDriverRepository.save(cab));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // Delete a cab
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCab(@PathVariable Long id) {
        return cabDriverRepository.findById(id)
            .map(cab -> {
                cabDriverRepository.delete(cab);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
