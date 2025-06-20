package com.cabrooster.config;

import com.cabrooster.model.CabDriver;
import com.cabrooster.model.Passenger;
import com.cabrooster.model.TransportAdministrator;
import com.cabrooster.repository.CabDriverRepository;
import com.cabrooster.repository.PassengerRepository;
import com.cabrooster.repository.TransportAdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CabDriverRepository cabDriverRepository;
    
    @Autowired
    private PassengerRepository passengerRepository;
    
    @Autowired
    private TransportAdministratorRepository adminRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize sample data
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Create sample cab drivers
        CabDriver cab1 = new CabDriver();
        cab1.setName("John Doe");
        cab1.setEmail("john.doe@example.com");
        cab1.setPhone("+1234567890");
        cab1.setLicenseNumber("DL123456");
        cab1.setCabNumber("KA01AB1234");
        cab1.setCurrentLatitude(12.9716);
        cab1.setCurrentLongitude(77.5946);
        cab1.setAvailable(true);
        
        CabDriver cab2 = new CabDriver();
        cab2.setName("Jane Smith");
        cab2.setEmail("jane.smith@example.com");
        cab2.setPhone("+1987654321");
        cab2.setLicenseNumber("DL654321");
        cab2.setCabNumber("KA01CD5678");
        cab2.setCurrentLatitude(12.9352);
        cab2.setCurrentLongitude(77.6245);
        cab2.setAvailable(true);
        
        cabDriverRepository.saveAll(Arrays.asList(cab1, cab2));
        
        // Create sample passengers
        Passenger passenger1 = new Passenger();
        passenger1.setName("Alice Johnson");
        passenger1.setEmail("alice.johnson@example.com");
        passenger1.setPhone("+1122334455");
        passenger1.setCurrentLatitude(12.9716);
        passenger1.setCurrentLongitude(77.5946);
        passenger1.setHomeLatitude(12.9667);
        passenger1.setHomeLongitude(77.5667);
        passenger1.setNeedsRide(true);
        
        Passenger passenger2 = new Passenger();
        passenger2.setName("Bob Williams");
        passenger2.setEmail("bob.williams@example.com");
        passenger2.setPhone("+5566778899");
        passenger2.setCurrentLatitude(12.9352);
        passenger2.setCurrentLongitude(77.6245);
        passenger2.setHomeLatitude(12.9252);
        passenger2.setHomeLongitude(77.6345);
        passenger2.setNeedsRide(true);
        
        passengerRepository.saveAll(Arrays.asList(passenger1, passenger2));
        
        // Create admin user
        TransportAdministrator admin = new TransportAdministrator();
        admin.setName("Admin User");
        admin.setEmail("admin@cabrooster.com");
        admin.setPhone("+919876543210");
        admin.setEmployeeId("EMP001");
        admin.setDepartment("Transport Management");
        
        adminRepository.save(admin);
        
        System.out.println("Sample data initialized successfully!");
    }
}
