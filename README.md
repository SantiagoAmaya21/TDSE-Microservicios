# TDSE Twitter-like Application

A secure Twitter-like application with microservices architecture and Auth0 authentication, built as part of the TDSE course assignment.

## Project Overview

This project demonstrates the evolution from a Spring Boot monolith to a serverless microservices architecture on AWS, with comprehensive security implementation using Auth0 for authentication and authorization.

### Architecture Evolution

1. **Monolith Phase**: Single Spring Boot application with RESTful API
2. **Microservices Phase**: Three independent serverless services deployed on AWS Lambda
3. **Security Integration**: Complete Auth0 integration with JWT-based authentication

## Features

### Core Functionality
- **Post Creation**: Users can create posts up to 140 characters
- **Public Stream**: Single global feed displaying all posts
- **User Management**: User profiles and authentication
- **Real-time Updates**: Dynamic content loading

### Security Features
- **Auth0 Authentication**: Secure login/logout with Auth0
- **JWT Validation**: Token-based API security
- **Role-based Access**: Protected endpoints with proper authorization
- **CORS Configuration**: Cross-origin resource sharing

### Technical Features
- **Swagger/OpenAPI**: Complete API documentation
- **Responsive Design**: Mobile-friendly React frontend
- **Serverless Architecture**: AWS Lambda deployment
- **Scalable Infrastructure**: Auto-scaling microservices

## Architecture Diagram

```
+-------------------+     +-------------------+     +-------------------+
|   React Frontend  |     |   Auth0 Service   |     |   AWS S3 Bucket   |
|   (Static Site)   |<--->|  (Authentication)|<--->|  (Frontend Host)  |
+-------------------+     +-------------------+     +-------------------+
         |                        |                        |
         |                        |                        |
         v                        v                        v
+-------------------+     +-------------------+     +-------------------+
|  API Gateway      |<--->|  JWT Tokens       |     |  CloudFront CDN   |
|  (Load Balancer)  |     |  (Bearer Auth)    |     |  (Performance)    |
+-------------------+     +-------------------+     +-------------------+
         |
         v
+-------------------+     +-------------------+     +-------------------+
|  User Service     |     |  Post Service     |     | Stream Service    |
|  (AWS Lambda)     |     |  (AWS Lambda)     |     |  (AWS Lambda)     |
+-------------------+     +-------------------+     +-------------------+
         |                        |                        |
         v                        v                        v
+-------------------+     +-------------------+     +-------------------+
|  User Database    |     |  Post Database    |     |  Cache Layer      |
|  (DynamoDB)       |     |  (DynamoDB)       |     |  (Redis)          |
+-------------------+     +-------------------+     +-------------------+
```

## Project Structure

```
TDSE-Microservicios/
|
|-- src/main/java/com/tdse/twitter/
|   |-- controller/          # REST API Controllers
|   |-- service/             # Business Logic Layer
|   |-- repository/          # Data Access Layer
|   |-- model/               # Entity Classes
|   |-- dto/                 # Data Transfer Objects
|   |-- config/              # Configuration Classes
|   |-- exception/           # Exception Handling
|   `-- TwitterLikeApplication.java
|
|-- frontend/                # React Frontend Application
|   |-- src/
|   |   |-- components/      # React Components
|   |   |-- services/        # API Services
|   |   `-- App.js          # Main Application
|   |-- public/             # Static Assets
|   `-- package.json        # Dependencies
|
|-- microservices/          # Serverless Microservices
|   |-- user-service/       # User Management Service
|   |-- post-service/       # Post Management Service
|   `-- stream-service/     # Stream/Feed Service
|
|-- deployment/             # AWS Deployment Scripts
|   |-- deploy.sh          # Linux/Mac Deployment
|   `-- deploy.bat         # Windows Deployment
|
`-- pom.xml                # Maven Configuration
```

## Technology Stack

### Backend (Monolith)
- **Spring Boot 3.2.5**: Main application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database operations
- **H2 Database**: In-memory database for development
- **Swagger/OpenAPI**: API documentation
- **Auth0 SDK**: Authentication integration

### Frontend
- **React 18**: User interface framework
- **Auth0 React SDK**: Authentication
- **Axios**: HTTP client
- **Tailwind CSS**: Styling
- **React Router**: Navigation

### Microservices & Cloud
- **AWS Lambda**: Serverless compute
- **AWS API Gateway**: API management
- **AWS S3**: Static website hosting
- **Serverless Framework**: Deployment automation
- **DynamoDB**: NoSQL database (production)

## API Documentation

### Authentication
All protected endpoints require a valid JWT Bearer token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### Endpoints

#### Public Endpoints (No Authentication Required)
- `GET /api/posts` - Get all posts in public stream
- `GET /api/stream` - Get public stream (alternative endpoint)
- `GET /api/users` - Get all users
- `GET /api/users/{username}` - Get user by username
- `GET /api/posts/{id}` - Get specific post by ID

#### Protected Endpoints (Authentication Required)
- `POST /api/posts` - Create a new post
- `DELETE /api/posts/{id}` - Delete a post (author only)
- `GET /api/users/me` - Get current user information

### API Models

#### Post Model
```json
{
  "id": 1,
  "content": "Hello, world!",
  "user": {
    "id": 1,
    "username": "john_doe",
    "displayName": "John Doe",
    "picture": "https://example.com/avatar.jpg"
  },
  "createdAt": "2024-04-07T12:00:00Z"
}
```

#### Create Post Request
```json
{
  "content": "This is my post content (max 140 characters)"
}
```

#### User Model
```json
{
  "id": 1,
  "auth0Id": "auth0|1234567890",
  "email": "user@example.com",
  "username": "john_doe",
  "displayName": "John Doe",
  "picture": "https://example.com/avatar.jpg",
  "createdAt": "2024-04-07T12:00:00Z"
}
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- Maven 3.8 or higher
- AWS CLI configured
- Auth0 account and domain

### 1. Clone the Repository
```bash
git clone <repository-url>
cd TDSE-Microservicios
```

### 2. Configure Auth0
1. Create a new Auth0 application (SPA type)
2. Create a new Auth0 API
3. Update configuration files with your Auth0 credentials:
   - Backend: `src/main/resources/application.properties`
   - Frontend: `frontend/.env.local`

### 3. Backend Setup
```bash
# Build the application
mvn clean install

# Run the monolith locally
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

### 4. Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Create environment file
cp .env.example .env.local
# Edit .env.local with your Auth0 credentials

# Run development server
npm start
```

The frontend will be available at `http://localhost:3000`

### 5. Access Swagger Documentation
Open `http://localhost:8080/swagger-ui.html` in your browser to explore the API documentation.

## AWS Deployment

### Prerequisites
- AWS CLI configured with appropriate permissions
- Serverless Framework installed (`npm install -g serverless`)
- Node.js and Maven installed

### Automated Deployment

#### Windows
```bash
deployment\deploy.bat
```

#### Linux/Mac
```bash
chmod +x deployment/deploy.sh
./deployment/deploy.sh
```

### Manual Deployment Steps

1. **Deploy Frontend to S3**
```bash
cd frontend
npm run build
aws s3 mb s3://your-bucket-name
aws s3 sync build/ s3://your-bucket-name --delete
aws s3 website s3://your-bucket-name --index-document index.html
```

2. **Deploy Microservices**
```bash
# For each service
cd microservices/user-service
mvn clean package -DskipTests
serverless deploy --auth0Domain YOUR_DOMAIN --auth0Audience YOUR_AUDIENCE
```

## Testing

### Running Tests
```bash
# Backend tests
mvn test

# Frontend tests
cd frontend
npm test
```

### Test Coverage
- Unit tests for all service layers
- Integration tests for API endpoints
- Frontend component tests
- End-to-end authentication flow

### API Testing Examples

#### Create a Post (Authenticated)
```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content": "Hello, Twitter!"}'
```

#### Get Public Stream (Public)
```bash
curl http://localhost:8080/api/posts
```

#### Get Current User (Authenticated)
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Security Implementation

### Auth0 Configuration
1. **SPA Application**: Configure for React frontend
2. **API Application**: Configure for Spring Boot backend
3. **Scopes**: Define `read:posts`, `write:posts`, `read:profile`
4. **Rules**: Implement custom logic if needed

### JWT Validation
- Spring Boot validates JWT tokens using Auth0 issuer URI
- Audience validation ensures tokens are intended for this API
- Automatic token refresh handled by Auth0 SDK

### Security Best Practices
- HTTPS enforced in production
- CORS properly configured
- Input validation on all endpoints
- SQL injection prevention through JPA
- XSS prevention in React components

## Performance Considerations

### Database Optimization
- Indexed queries for user lookups
- Pagination for large post feeds
- Connection pooling configured

### Caching Strategy
- Redis for session storage (production)
- Browser caching for static assets
- CDN distribution via CloudFront

### Lambda Optimization
- Cold start mitigation with warmup
- Memory allocation optimized per service
- Timeout settings balanced for performance

## Monitoring and Logging

### Application Monitoring
- Spring Boot Actuator endpoints
- CloudWatch metrics for Lambda functions
- Error tracking and alerting

### Logging Strategy
- Structured logging with correlation IDs
- Security event logging
- Performance metrics collection

## Troubleshooting

### Common Issues

#### Authentication Failures
- Verify Auth0 domain and audience configuration
- Check JWT token expiration
- Ensure proper CORS configuration

#### Deployment Issues
- Verify AWS credentials and permissions
- Check Lambda function logs in CloudWatch
- Validate Serverless Framework configuration

#### Database Issues
- Check database connection settings
- Verify schema creation
- Monitor connection pool usage

### Debug Mode
Enable debug logging by setting:
```properties
logging.level.com.tdse.twitter=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Future Enhancements

### Planned Features
- Real-time notifications
- Post likes and comments
- User following system
- Media attachments
- Advanced search functionality

### Technical Improvements
- GraphQL API implementation
- Event-driven architecture
- Advanced caching strategies
- Multi-region deployment

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Team

- **TDSE Team** - Development and Architecture
- **Course** - TDSE (Tercio)
- **Semester** - 2026-1

## Live Demo

**Frontend URL**: [Deployed S3 Website URL]
**API Documentation**: [Swagger UI URL]
**API Base URL**: [API Gateway URL]

*Note: Replace the URLs above with your actual deployed URLs after deployment.*

## Video Demonstration

A 5-8 minute video demonstration is available showing:
- Complete authentication flow with Auth0
- Post creation and viewing functionality
- User profile management
- Architecture overview and security configuration
- Deployment process walkthrough

---

**Last Updated**: April 7, 2026
**Version**: 1.0.0