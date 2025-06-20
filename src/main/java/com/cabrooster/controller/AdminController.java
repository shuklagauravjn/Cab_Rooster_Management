package com.cabrooster.controller;

import com.cabrooster.model.CabDriver;
import com.cabrooster.model.Passenger;
import com.cabrooster.model.TransportAdministrator;
import com.cabrooster.model.RideAssignment;
import com.cabrooster.repository.CabDriverRepository;
import com.cabrooster.repository.PassengerRepository;
import com.cabrooster.repository.RideAssignmentRepository;
import com.cabrooster.repository.TransportAdministratorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "APIs for administrative operations")
public class AdminController {

    @Autowired
    private CabDriverRepository cabDriverRepository;
    
    @Autowired
    private PassengerRepository passengerRepository;
    
    @Autowired
    private TransportAdministratorRepository adminRepository;
    
    @Autowired
    private RideAssignmentRepository rideAssignmentRepository;

    @Operation(summary = "Get system statistics", description = "Retrieves various statistics about the system including counts of cabs, passengers, and rides")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total counts
        stats.put("totalCabs", cabDriverRepository.count());
        stats.put("totalPassengers", passengerRepository.count());
        stats.put("totalAdmins", adminRepository.count());
        
        // Active counts
        stats.put("availableCabs", cabDriverRepository.countByAvailable(true));
        stats.put("passengersNeedingRide", passengerRepository.countByNeedsRide(true));
        
        // Ride statistics
        stats.put("totalRides", rideAssignmentRepository.count());
        stats.put("pendingRides", rideAssignmentRepository.countByStatus("PENDING"));
        stats.put("inProgressRides", rideAssignmentRepository.countByStatus("IN_PROGRESS"));
        stats.put("completedRides", rideAssignmentRepository.countByStatus("COMPLETED"));
        
        return stats;
    }

    @Operation(summary = "Set passenger's home location", description = "Updates the home location coordinates for a specific passenger")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated passenger's home location",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Passenger.class))),
        @ApiResponse(responseCode = "404", description = "Passenger not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping(value = "/passengers/{id}/home-location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Passenger> setPassengerHomeLocation(
            @Parameter(description = "ID of the passenger to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Latitude of the home location", required = true)
            @RequestParam double latitude,
            @Parameter(description = "Longitude of the home location", required = true)
            @RequestParam double longitude) {
        
        return passengerRepository.findById(id)
            .map(passenger -> {
                passenger.setHomeLatitude(latitude);
                passenger.setHomeLongitude(longitude);
                return ResponseEntity.ok(passengerRepository.save(passenger));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all administrators", description = "Retrieves a list of all transport administrators")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of administrators",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = TransportAdministrator[].class)))
    @GetMapping(value = "/administrators", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TransportAdministrator> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Operation(summary = "Create a new administrator", description = "Creates a new transport administrator account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created administrator",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = TransportAdministrator.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping(value = "/administrators", 
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public TransportAdministrator createAdmin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Transport Administrator object to be created",
                required = true,
                content = @Content(schema = @Schema(implementation = TransportAdministrator.class))
            )
            @RequestBody TransportAdministrator admin) {
        return adminRepository.save(admin);
    }

    @Operation(summary = "Get all ride assignments", description = "Retrieves a list of all ride assignments")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of ride assignments",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = RideAssignment[].class)))
    @GetMapping(value = "/rides", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RideAssignment> getAllRides() {
        return rideAssignmentRepository.findAll();
    }
    
    @Operation(summary = "Force assign a ride", description = "Admin override to manually assign a ride to a cab")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully assigned ride",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = RideAssignment.class))),
        @ApiResponse(responseCode = "404", description = "Cab or passenger not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping(value = "/rides/assign", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideAssignment> forceAssignRide(
            @Parameter(description = "ID of the cab to assign", required = true)
            @RequestParam Long cabId,
            @Parameter(description = "ID of the passenger for the ride", required = true)
            @RequestParam Long passengerId) {
        
        return cabDriverRepository.findById(cabId).flatMap(cab -> 
            passengerRepository.findById(passengerId).map(passenger -> {
                RideAssignment assignment = new RideAssignment();
                assignment.setCabDriver(cab);
                assignment.setPassenger(passenger);
                assignment.setStatus("PENDING");
                
                // Mark cab as unavailable
                cab.setAvailable(false);
                cabDriverRepository.save(cab);
                
                // Mark passenger as assigned
                passenger.setNeedsRide(false);
                passengerRepository.save(passenger);
                
                return rideAssignmentRepository.save(assignment);
            })
        ).map(ResponseEntity::ok)
        .orElse(ResponseEntity.badRequest().build());
    }
}
