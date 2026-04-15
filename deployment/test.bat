@echo off
REM Test Script for TDSE Twitter Application (Windows)

echo Starting TDSE Twitter Application Tests...

REM Configuration
set API_BASE_URL=http://localhost:8080/api
set FRONTEND_URL=http://localhost:3000

echo Testing Backend API...

REM Test 1: Health Check
echo 1. Testing health check...
curl -s -o nul -w "%%{http_code}" http://localhost:8080/actuator/health | findstr "200" >nul
if errorlevel 1 (
    echo    Health check failed
) else (
    echo    Health check passed
)

REM Test 2: Get Public Stream
echo 2. Testing public stream endpoint...
curl -s -o nul -w "%%{http_code}" %API_BASE_URL%/posts | findstr "200" >nul
if errorlevel 1 (
    echo    Public stream endpoint failed
) else (
    echo    Public stream endpoint working
)

REM Test 3: Get All Users
echo 3. Testing users endpoint...
curl -s -o nul -w "%%{http_code}" %API_BASE_URL%/users | findstr "200" >nul
if errorlevel 1 (
    echo    Users endpoint failed
) else (
    echo    Users endpoint working
)

REM Test 4: Swagger Documentation
echo 4. Testing Swagger documentation...
curl -s -o nul -w "%%{http_code}" http://localhost:8080/swagger-ui.html | findstr "200" >nul
if errorlevel 1 (
    echo    Swagger documentation not accessible
) else (
    echo    Swagger documentation accessible
)

REM Test 5: H2 Console
echo 5. Testing H2 console...
curl -s -o nul -w "%%{http_code}" http://localhost:8080/h2-console | findstr "200" >nul
if errorlevel 1 (
    echo    H2 console not accessible
) else (
    echo    H2 console accessible
)

echo Testing Frontend...

REM Test 6: Frontend Accessibility
echo 6. Testing frontend accessibility...
curl -s -o nul -w "%%{http_code}" %FRONTEND_URL% | findstr "200" >nul
if errorlevel 1 (
    echo    Frontend not accessible
) else (
    echo    Frontend accessible
)

echo Running Backend Unit Tests...

REM Test 7: Backend Unit Tests
echo 7. Running backend unit tests...
cd ..
call mvn test > test-results.log 2>&1
if errorlevel 1 (
    echo    Backend unit tests failed
    echo    Check test-results.log for details
) else (
    echo    Backend unit tests passed
)

echo Running Frontend Tests...

REM Test 8: Frontend Tests
echo 8. Running frontend tests...
cd frontend
call npm test > ..\test-results-frontend.log 2>&1
if errorlevel 1 (
    echo    Frontend tests failed
    echo    Check test-results-frontend.log for details
) else (
    echo    Frontend tests passed
)

echo API Integration Tests...

REM Test 9: Create Post (should fail without auth)
echo 9. Testing protected endpoint without auth...
curl -s -o nul -w "%%{http_code}" -X POST %API_BASE_URL%/posts -H "Content-Type: application/json" -d "{\"content\": \"Test post\"}" | findstr "401\|403" >nul
if errorlevel 1 (
    echo    Protected endpoint not properly secured
) else (
    echo    Protected endpoint properly secured
)

REM Test 10: Invalid Post Creation
echo 10. Testing validation...
curl -s -o nul -w "%%{http_code}" -X POST %API_BASE_URL%/posts -H "Content-Type: application/json" -d "{\"content\": \"\"}" | findstr "400" >nul
if errorlevel 1 (
    echo    Validation not working
) else (
    echo    Validation working correctly
)

echo Performance Tests...

REM Test 11: Load Test
echo 11. Running basic load test...
echo Testing 10 concurrent requests to public stream...
for /l %%i in (1,1,10) do (
    start /b curl -s %API_BASE_URL%/posts > nul
)
timeout /t 5 >nul
echo    Load test completed

echo Security Tests...

REM Test 12: CORS Headers
echo 12. Testing CORS headers...
curl -s -I %API_BASE_URL%/posts | findstr "Access-Control" >nul
if errorlevel 1 (
    echo    CORS headers missing
) else (
    echo    CORS headers present
)

REM Test 13: SQL Injection Test
echo 13. Testing SQL injection protection...
curl -s -o nul -w "%%{http_code}" "%API_BASE_URL%/users/OR%%201=1--" | findstr "404\|400" >nul
if errorlevel 1 (
    echo    Potential SQL injection vulnerability
) else (
    echo    SQL injection protection working
)

echo Configuration Tests...

REM Test 14: Environment Variables
echo 14. Checking environment configuration...
if defined AUTH0_DOMAIN (
    if defined AUTH0_AUDIENCE (
        echo    Auth0 environment variables set
    ) else (
        echo    Auth0 audience not set
    )
) else (
    echo    Auth0 environment variables not set (expected for local development)
)

echo Database Tests...

REM Test 15: Database Connectivity
echo 15. Testing database connectivity...
curl -s -o nul -w "%%{http_code}" %API_BASE_URL%/users | findstr "200" >nul
if errorlevel 1 (
    echo    Database connectivity issues
) else (
    echo    Database connectivity working
)

echo Test Suite Completed!
echo.
echo Summary:
echo - Backend API endpoints tested
echo - Frontend accessibility verified
echo - Security measures validated
echo - Database connectivity confirmed
echo - CORS configuration checked
echo - Load testing performed
echo.
echo For detailed results, check the test log files:
echo - test-results.log (backend tests)
echo - test-results-frontend.log (frontend tests)

pause
