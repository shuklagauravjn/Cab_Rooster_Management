package com.cabrooster.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ride_assignments")
public class RideAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cab_driver_id")
    private CabDriver cabDriver;
    
    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;
    
    private LocalDateTime assignmentTime;
    private LocalDateTime completionTime;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CabDriver getCabDriver() {
        return cabDriver;
    }

    public void setCabDriver(CabDriver cabDriver) {
        this.cabDriver = cabDriver;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public LocalDateTime getAssignmentTime() {
        return assignmentTime;
    }

    public void setAssignmentTime(LocalDateTime assignmentTime) {
        this.assignmentTime = assignmentTime;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
