#!/bin/bash

# Docker Development Setup Script for TDSE Twitter Application

set -e

echo "Setting up TDSE Twitter Application with Docker..."

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Create environment file if it doesn't exist
if [ ! -f .env ]; then
    echo "Creating .env file from template..."
    cp .env.example .env
    echo "Please edit .env file with your Auth0 credentials before proceeding."
    echo "Required variables:"
    echo "  - AUTH0_DOMAIN"
    echo "  - AUTH0_CLIENT_ID"
    echo "  - AUTH0_AUDIENCE"
    echo ""
    echo "Press Enter to continue after editing .env file..."
    read
fi

# Build and start services
echo "Building and starting Docker containers..."
docker-compose up --build -d

# Wait for services to be ready
echo "Waiting for services to start..."
sleep 30

# Check if services are running
echo "Checking service status..."
docker-compose ps

# Test backend health
echo "Testing backend health..."
if curl -f http://localhost:8080/api/health > /dev/null 2>&1; then
    echo "Backend is healthy!"
else
    echo "Backend health check failed. Check logs with: docker-compose logs backend"
fi

# Test frontend
echo "Testing frontend..."
if curl -f http://localhost:3000 > /dev/null 2>&1; then
    echo "Frontend is accessible!"
else
    echo "Frontend not accessible. Check logs with: docker-compose logs frontend"
fi

echo ""
echo "Setup complete! Application is running at:"
echo "  Frontend: http://localhost:3000"
echo "  Backend API: http://localhost:8080/api"
echo "  Swagger UI: http://localhost:8080/swagger-ui.html"
echo "  H2 Console: http://localhost:8080/h2-console (only in dev mode)"
echo ""
echo "Useful commands:"
echo "  View logs: docker-compose logs -f [service]"
echo "  Stop services: docker-compose down"
echo "  Restart services: docker-compose restart"
echo "  Access database: docker-compose exec postgres psql -U postgres -d twitter_db"
