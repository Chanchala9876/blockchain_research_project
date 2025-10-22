# Blockchain Research Project - Backend

This is the backend API for the Blockchain Research Project, built with Spring Boot and integrated with Hyperledger Fabric for blockchain functionality.

## Features

- **Multi-University Support**: Complete institute-aware architecture with separate data for different universities
- **AI-Powered Thesis Verification**: Advanced similarity detection using Ollama AI with role-based reporting
- **Blockchain Integration**: Hyperledger Fabric integration for immutable research paper records
- **Enhanced Security**: JWT-based authentication with role-based access control
- **Document Processing**: Support for both PDF and DOCX document uploads and text extraction

## Technology Stack

- **Framework**: Spring Boot 3.1.5
- **Database**: MongoDB with Spring Data
- **Authentication**: JWT with Spring Security
- **AI Integration**: Ollama (nomic-embed-text model) for similarity analysis
- **Blockchain**: Hyperledger Fabric integration
- **Document Processing**: Apache POI for DOCX, PDFBox for PDF
- **Build Tool**: Maven

## Key Models

- **Institute**: Multi-university support with institute-specific data segregation
- **Admin/Professor/User**: Role-based users with institute associations
- **ResearchPaper**: Papers linked to specific institutes with blockchain integration
- **ThesisVerification**: AI-powered similarity checking with enhanced thresholds

## Recent Enhancements

### Multi-University Architecture ✅
- Added `instituteId` fields to all models (Admin, User, Professor, ResearchPaper)
- Institute-aware upload validation and data segregation
- Cross-institute similarity checking capabilities

### Enhanced Similarity Detection ✅
- Lowered similarity thresholds from 30% to 15% for better detection
- Role-based reporting (Professors see author details, Students see percentages only)
- Support for both PDF and Word document processing
- Improved duplicate detection with content similarity analysis

### Fixed Admin Upload Issues ✅
- Resolved 400 errors in admin uploads by ensuring proper `instituteId` assignment
- Enhanced data initialization with automatic admin-institute linking
- Proper validation for cross-institute upload prevention

## Quick Start

1. **Prerequisites**:
   - Java 17+
   - MongoDB running on localhost:27017
   - Ollama with nomic-embed-text model installed

2. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the API**: http://localhost:8090

## Demo Accounts

All demo accounts use password: `1234`

### JNU Admins:
- `admin.super@jnu.ac.in` - Super Admin
- `admin.system@jnu.ac.in` - System Admin

### IIT Delhi Admins:
- `admin.academic@iitd.ac.in` - Academic Admin
- `admin.research@iitd.ac.in` - Research Admin

## API Endpoints

- **Authentication**: `/api/auth/*`
- **Admin Operations**: `/api/admin/*`
- **Research Papers**: `/api/research-papers/*`
- **Thesis Verification**: `/api/thesis/verify`
- **Blockchain**: `/api/blockchain/*`

## Configuration

Key configuration files:
- `application.properties` - Database and application settings
- `fabric/network-config.yaml` - Hyperledger Fabric configuration
- `DataInitializer.java` - Demo data setup with multi-institute support

## Development Status

- ✅ Multi-university architecture implemented
- ✅ Enhanced similarity detection with role-based reporting
- ✅ Admin upload validation fixed
- ✅ Blockchain integration working (simulation mode)
- ⏳ Institute-aware similarity comparison (future enhancement)

For setup instructions, see `FABRIC_SETUP.md` and `MONGODB_SETUP.md`.