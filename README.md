# 📚 UMS Finance System Documentation

## Project Overview

The UMS Finance System is a JavaFX-based desktop application designed to manage university student invoices, including course fees, sports activities, and food costs. The system provides a user-friendly interface for administrators to track and manage financial records.

## Key Features

1. Secure Login System
2. Dashboard with Real-time Statistics
3. Interactive Data Visualization
   - Pie Chart for Total Costs
   - Bar Chart for University-specific Costs
4. Invoice Management
   - Create New Invoices
   - View Invoice Details
   - Search and Filter Functionality
5. Data Filtering Options
   - By Year
   - By Month
   - By University

### Other Components

- Applicant Details
- Attendance Monitoring
- Course Selection
- **Finance**
- Restaurants
- Sports School

## Actors

## Use Cases

## Class Diagram

## Technical Implementation

### Architecture

- **Frontend**: JavaFX with FXML
- **Backend**: Java
- **Database**: SQLite
- **Build System**: Maven

### Key Components

1. **LoginController**

   - Handles user authentication
   - Manages login view transitions
   - Currently using simplified authentication for demo purposes

2. **DashboardController**

   - Main application controller
   - Manages data visualization
   - Handles invoice management
   - Implements search and filter functionality

3. **DatabaseController**
   - Manages database connections
   - Handles CRUD operations
   - Provides data access methods

### File Structure

/_
├── .gitignore
├── .idea
├── misc.xml
├── sqldialects.xml
└── workspace.xml
├── README.md
├── mvnw.cmd
├── pom.xml
└── src
└── main
├── java
├── com
│ └── example
│ │ └── dashboard
│ │ ├── DashboardApp.java
│ │ ├── DashboardController.java
│ │ ├── GenerateNewInvoiceController.java
│ │ ├── Invoice.java
│ │ └── ViewInvoiceController.java
└── module-info.java
└── resources
├── application.properties
└── com
└── example
└── dashboard
├── assets
└── icons
│ └── trash-filled.png
├── dashboard-view.fxml
├── generate-invoice-view.fxml
├── invoice-details-view.fxml
└── styles.css
_/

### Development Challenges and Solutions

1. **BCrypt Integration Issue**

   - **Challenge**: Initial implementation used BCrypt for password hashing
   - **Solution**: Temporarily switched to plain text passwords for development

2. **JavaFX Module Dependencies**

   - **Challenge**: Module visibility issues with external libraries
   - **Solution**: Updated module-info.java and pom.xml configurations
   - **Impact**: Improved build stability

3. **Data Visualization**

   - **Challenge**: Chart positioning and label overlapping
   - **Solution**: Implemented custom positioning logic for chart labels
   - **Result**: Better visual presentation of data

4. **Performance Optimization**
   - **Challenge**: Slow data loading with large datasets
   - **Solution**: Implemented efficient data filtering and caching
   - **Impact**: Improved application responsiveness

### Code Quality and Testing

#### Code Quality Measures

1. **Modularity**

   - Separate controllers for different views
   - Clear separation of concerns
   - Reusable components

2. **Error Handling**

   - Comprehensive try-catch blocks
   - User-friendly error messages
   - Logging for debugging

3. **Code Style**
   - Consistent naming conventions
   - Clear method organization
   - Comprehensive comments

#### Testing

1. **Manual Testing**

   - UI/UX testing
   - Data validation testing
   - Cross-platform testing

2. **JUnit Tests** (To Be Implemented)
   - Unit tests for controllers
   - Database operation tests
   - Authentication tests

### Setup and Installation

1. **Prerequisites**

   - Java 17 or higher
   - Maven
   - SQLite

2. **Build Instructions**

```bash
mvn clean install
mvn javafx:run
```

3. **Database Setup**
   - SQLite database file: UMS-DB.db
   - Initial admin credentials:
     - Username: admin@ums.com
     - Password: uni123

### Project Status

- **Current Version**: 1.0-SNAPSHOT
- **Last Updated**: March 2025
- **Status**: Development/Testing

## Interface

## Finance Component UML

## Conclusion

The UMS Finance System provides a solid foundation for university finance management. While there are areas for improvement, the current implementation successfully demonstrates core functionality and provides a user-friendly interface for managing student invoices and financial records.
