@echo off
REM Docker Development Setup Script for TDSE Twitter Application (Windows)

echo Setting up TDSE Twitter Application with Docker...

REM Check if Docker is installed
docker --version >nul 2>&1
if errorlevel 1 (
    echo Docker is not installed. Please install Docker first.
    pause
    exit /b 1
)

REM Check if Docker Compose is installed
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo Docker Compose is not installed. Please install Docker Compose first.
    pause
    exit /b 1
)

REM Create environment file if it doesn't exist
if not exist .env (
    echo Creating .env file from template...
    copy .env.example .env
    echo Please edit .env file with your Auth0 credentials before proceeding.
    echo Required variables:
    echo   - AUTH0_DOMAIN
    echo   - AUTH0_CLIENT_ID
    echo   - AUTH0_AUDIENCE
    echo.
    echo Press Enter to continue after editing .env file...
    pause
)

REM Build and start services
echo Building and starting Docker containers...
docker-compose up --build -d

REM Wait for services to be ready
echo Waiting for services to start...
timeout /t 30 /nobreak >nul

REM Check if services are running
echo Checking service status...
docker-compose ps

REM Test backend health
echo Testing backend health...
curl -f http://localhost:8080/api/health >nul 2>&1
if errorlevel 1 (
    echo Backend health check failed. Check logs with: docker-compose logs backend
) else (
    echo Backend is healthy!
)

REM Test frontend
echo Testing frontend...
curl -f http://localhost:3000 >nul 2>&1
if errorlevel 1 (
    echo Frontend not accessible. Check logs with: docker-compose logs frontend
) else (
    echo Frontend is accessible!
)

echo.
echo Setup complete! Application is running at:
echo   Frontend: http://localhost:3000
echo   Backend API: http://localhost:8080/api
echo   Swagger UI: http://localhost:8080/swagger-ui.html
echo.
echo Useful commands:
echo   View logs: docker-compose logs -f [service]
echo   Stop services: docker-compose down
echo   Restart services: docker-compose restart
echo   Access database: docker-compose exec postgres psql -U postgres -d twitter_db

pause
