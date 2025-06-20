package com.cabrooster.repository;

import com.cabrooster.model.TransportAdministrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportAdministratorRepository extends JpaRepository<TransportAdministrator, Long> {
    // Custom query methods can be added here if needed
}
