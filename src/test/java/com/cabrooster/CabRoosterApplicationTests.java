package com.cabrooster;

import com.cabrooster.model.CabDriver;
import com.cabrooster.model.Passenger;
import com.cabrooster.repository.CabDriverRepository;
import com.cabrooster.repository.PassengerRepository;
import com.cabrooster.util.GeoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.h2.console.enabled=true",
    "spring.jpa.hibernate.ddl-auto=update",
    "spring.jpa.show-sql=true",
    "spring.jpa.properties.hibernate.format_sql=true"
})
class CabRoosterApplicationTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CabDriverRepository cabDriverRepository;
    
    @Autowired
    private PassengerRepository passengerRepository;

    @BeforeEach
    void setUp() {
        // Create and save test data
        CabDriver driver = new CabDriver();
        driver.setName("Test Driver");
        driver.setEmail("driver@test.com");
        driver.setPhone("1234567890");
        driver.setLicenseNumber("DL123456");
        driver.setAvailable(true);
        entityManager.persist(driver);
        
        Passenger passenger = new Passenger();
        passenger.setName("Test Passenger");
        passenger.setEmail("passenger@test.com");
        passenger.setPhone("0987654321");
        entityManager.persist(passenger);
        
        entityManager.flush();
    }
    
    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully
        assertNotNull(cabDriverRepository);
        assertNotNull(passengerRepository);
    }
    
    @Test
    void testGeoUtils() {
        // Test distance calculation between two points
        double distance = GeoUtils.calculateDistanceInMeters(
            12.9716, 77.5946, // Bangalore coordinates
            12.9667, 77.5667  // Nearby point in Bangalore
        );
        
        // The distance should be approximately 3km (3000m) with some tolerance
        assertTrue(distance > 2000 && distance < 4000, "Distance calculation is not within expected range");
    }
    
    @Test
    void testSampleDataLoaded() {
        // Check if sample data is loaded
        assertTrue(cabDriverRepository.count() > 0, "No cab drivers found in the database");
        assertTrue(passengerRepository.count() > 0, "No passengers found in the database");
        
        // Check if at least one cab is available
        assertTrue(cabDriverRepository.findByAvailable(true).size() > 0, 
                 "No available cabs found in the sample data");
    }
}
