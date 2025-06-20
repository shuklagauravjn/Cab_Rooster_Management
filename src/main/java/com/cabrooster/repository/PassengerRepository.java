package com.cabrooster.repository;

import com.cabrooster.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    List<Passenger> findByNeedsRide(boolean needsRide);

    long countByNeedsRide(boolean needsRide);
}
