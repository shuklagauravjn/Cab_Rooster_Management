# Cab Rooster Management System

A Spring Boot application for managing cab assignments and passenger rides with real-time location tracking.

## Features

- **Cab Driver Management**: Track cab drivers and their current locations
- **Passenger Management**: Manage passengers and their home locations
- **Ride Assignment**: Automatically assign the nearest available cab to passengers
- **Administrative Dashboard**: Monitor system statistics and manage users
- **Scheduled Operations**: Automatically create ride plans at 8:00 AM and 7:00 PM daily

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- H2 Database (embedded)

## Getting Started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd cab-rooster-management
   ```

2. **Build the application**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, you can access:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:cabroosterdb`
  - Username: `sa`
  - Password: `password`

## API Endpoints

### Cabs
- `GET /api/cabs` - Get all cabs
- `POST /api/cabs` - Create a new cab
- `GET /api/cabs/{id}` - Get cab by ID
- `PUT /api/cabs/{id}` - Update cab details
- `PUT /api/cabs/{id}/location` - Update cab location (call every hour)

### Passengers
- `GET /api/passengers` - Get all passengers
- `POST /api/passengers` - Create a new passenger
- `GET /api/passengers/{id}` - Get passenger by ID
- `PUT /api/passengers/{id}/location` - Update passenger location (call every 15 minutes)
- `POST /api/passengers/{id}/request-ride` - Request a ride

### Rides
- `GET /api/rides` - Get all ride assignments
- `GET /api/rides/{id}` - Get ride assignment by ID
- `PUT /api/rides/{id}/status` - Update ride status

### Admin
- `GET /api/admin/statistics` - Get system statistics
- `GET /api/admin/administrators` - Get all administrators
- `POST /api/admin/administrators` - Create a new administrator
- `GET /api/admin/rides` - Get all rides (admin view)
- `POST /api/admin/rides/assign` - Force assign a ride (admin override)

## Scheduling

The system automatically runs ride assignments at:
- 8:00 AM daily
- 7:00 PM daily

## Data Model

### CabDriver
- id: Long
- name: String
- email: String
- phone: String
- licenseNumber: String
- cabNumber: String
- currentLatitude: Double
- currentLongitude: Double
- available: Boolean

### Passenger
- id: Long
- name: String
- email: String
- phone: String
- currentLatitude: Double
- currentLongitude: Double
- homeLatitude: Double
- homeLongitude: Double
- needsRide: Boolean

### RideAssignment
- id: Long
- cabDriver: CabDriver
- passenger: Passenger
- assignmentTime: LocalDateTime
- completionTime: LocalDateTime
- status: String (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
