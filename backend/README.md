# Blockchain Research Project - Backend

This is the backend API for the Blockchain Research Project, built with Spring Boot and integrated with Hyperledger Fabric for blockchain functionality.

## 🚀 Features

### ✅ Multi-University Support
- **Institute-aware architecture**: Complete data segregation by university/institute
- **Cross-institute functionality**: Admins, professors, and students linked to specific institutes
- **Institute-specific uploads**: Papers automatically associated with admin's institute

### ✅ AI-Powered Thesis Verification
- **Advanced similarity detection**: Using Ollama AI (nomic-embed-text model)
- **Enhanced thresholds**: Improved from 30% to 15% for better small-change detection
- **Role-based reporting**: 
  - **Professors**: See full author details and university information
  - **Students**: See only similarity percentages for privacy

### ✅ Document Processing
- **PDF Support**: Advanced text extraction using PDFBox
- **DOCX Support**: Microsoft Word document processing with Apache POI
- **Unified processing**: Seamless handling of both document types

### ✅ Blockchain Integration
- **Hyperledger Fabric**: Immutable research paper records
- **Simulation mode**: Works without Fabric network for development
- **Paper hashing**: SHA-256 content verification

### ✅ Security & Authentication
- **JWT-based authentication**: Secure token-based access
- **Role-based access control**: Admin, Professor, Student roles
- **Cross-site protection**: CORS configuration and security filters

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.1.5
- **Database**: MongoDB with Spring Data
- **Authentication**: JWT with Spring Security  
- **AI Integration**: Ollama (nomic-embed-text model)
- **Blockchain**: Hyperledger Fabric
- **Document Processing**: Apache POI (DOCX), PDFBox (PDF)
- **Build Tool**: Maven

## 📊 Database Models

### Institute
- Multi-university support with complete institutional data
- Links to admins, professors, and research papers

### User Roles
- **Admin**: University administrators with institute-specific access
- **Professor**: Faculty members with research oversight capabilities  
- **User/Student**: Students with thesis submission and verification access

### ResearchPaper
- Institute-linked papers with blockchain integration
- AI embeddings for similarity analysis
- Comprehensive metadata and file storage

## 🔧 Quick Start

### Prerequisites
- **Java 17+** 
- **MongoDB** running on localhost:27017
- **Ollama** with nomic-embed-text model installed
- **Docker** (optional, for Hyperledger Fabric)

### Installation & Running

1. **Clone and navigate**:
   ```bash
   cd backend
   ```

2. **Install dependencies and run**:
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the API**: http://localhost:8090

## 👥 Demo Accounts

All demo accounts use password: `1234`

### JNU University
- **Super Admin**: `admin.super@jnu.ac.in`
- **System Admin**: `admin.system@jnu.ac.in`

### IIT Delhi  
- **Academic Admin**: `admin.academic@iitd.ac.in`
- **Research Admin**: `admin.research@iitd.ac.in`

## 🔗 API Endpoints

| Endpoint | Description |
|----------|-------------|
| `/api/auth/*` | Authentication (login, register) |
| `/api/admin/*` | Admin operations and management |
| `/api/research-papers/*` | Paper upload and management |
| `/api/thesis/verify` | AI-powered thesis verification |
| `/api/blockchain/*` | Blockchain record operations |
| `/api/institutes/*` | Institute management |

## ⚙ Configuration Files

- `application.properties` - Database and application settings
- `fabric/network-config.yaml` - Hyperledger Fabric configuration  
- `DataInitializer.java` - Demo data with multi-institute setup

## 🎯 Recent Enhancements

### Fixed Issues ✅
- **Admin Upload Validation**: Resolved 400 errors by ensuring proper `instituteId` assignment
- **Similarity Detection**: Enhanced thresholds and role-based messaging
- **Data Initialization**: Improved admin-institute linking and validation

### New Features ✅  
- **Multi-University Architecture**: Complete institute-aware data segregation
- **Enhanced AI Analysis**: Better similarity detection with contextual reporting
- **Document Format Support**: Both PDF and Word document processing
- **Role-Based Security**: Granular access control by user type

## 📈 Development Status

- ✅ **Multi-university architecture** - Complete
- ✅ **Enhanced similarity detection** - Complete  
- ✅ **Admin upload validation** - Fixed
- ✅ **Blockchain integration** - Working (simulation mode)
- ✅ **Document processing** - PDF + DOCX support
- 🔄 **Institute-aware similarity** - Future enhancement

## 📚 Setup Guides

- **Fabric Setup**: See `FABRIC_SETUP.md`
- **MongoDB Setup**: See `MONGODB_SETUP.md`
- **Application Help**: See `HELP.md`

## 🏗 Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│                   Frontend (React)                  │
└─────────────────┬───────────────────────────────────┘
                  │ HTTP/REST API
┌─────────────────▼───────────────────────────────────┐
│                Spring Boot Backend                  │
│  ┌─────────────┬─────────────┬─────────────────────┐ │
│  │ Controllers │  Services   │     Repositories    │ │
│  └─────────────┴─────────────┴─────────────────────┘ │
└─────────────────┬───────────────────────────┬───────┘
                  │                           │
        ┌─────────▼─────────┐       ┌─────────▼─────────┐
        │     MongoDB       │       │  Hyperledger      │
        │   (Data Store)    │       │     Fabric        │
        └───────────────────┘       └───────────────────┘
                  │
        ┌─────────▼─────────┐
        │      Ollama       │
        │   (AI Analysis)   │
        └───────────────────┘
```

This backend provides a comprehensive, scalable solution for multi-university thesis verification with advanced AI capabilities and blockchain integration.