package com.cabrooster.repository;

import com.cabrooster.model.RideAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideAssignmentRepository extends JpaRepository<RideAssignment, Long> {
    List<RideAssignment> findByStatus(String status);
    List<RideAssignment> findByCabDriverIdAndStatus(Long cabDriverId, String status);
    List<RideAssignment> findByPassengerIdAndStatus(Long passengerId, String status);
    long countByStatus(String status);
    List<RideAssignment> findByPassengerIdAndStatusIn(Long passengerId, List<String> of);
}
