package com.cabrooster.repository;

import com.cabrooster.model.CabDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CabDriverRepository extends JpaRepository<CabDriver, Long> {
    List<CabDriver> findByAvailable(boolean available);
    long countByAvailable(boolean available);
}
