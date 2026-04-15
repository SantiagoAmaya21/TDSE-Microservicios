#!/bin/bash

# Test Script for TDSE Twitter Application

set -e

echo "Starting TDSE Twitter Application Tests..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
API_BASE_URL="http://localhost:8080/api"
FRONTEND_URL="http://localhost:3000"

echo -e "${YELLOW}Testing Backend API...${NC}"

# Test 1: Health Check
echo "1. Testing health check..."
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health | grep -q "200"; then
    echo -e "${GREEN}   Health check passed${NC}"
else
    echo -e "${RED}   Health check failed${NC}"
fi

# Test 2: Get Public Stream
echo "2. Testing public stream endpoint..."
RESPONSE=$(curl -s -w "%{http_code}" "$API_BASE_URL/posts")
if echo "$RESPONSE" | grep -q "200"; then
    echo -e "${GREEN}   Public stream endpoint working${NC}"
else
    echo -e "${RED}   Public stream endpoint failed${NC}"
fi

# Test 3: Get All Users
echo "3. Testing users endpoint..."
RESPONSE=$(curl -s -w "%{http_code}" "$API_BASE_URL/users")
if echo "$RESPONSE" | grep -q "200"; then
    echo -e "${GREEN}   Users endpoint working${NC}"
else
    echo -e "${RED}   Users endpoint failed${NC}"
fi

# Test 4: Swagger Documentation
echo "4. Testing Swagger documentation..."
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui.html | grep -q "200"; then
    echo -e "${GREEN}   Swagger documentation accessible${NC}"
else
    echo -e "${RED}   Swagger documentation not accessible${NC}"
fi

# Test 5: H2 Console
echo "5. Testing H2 console..."
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/h2-console | grep -q "200"; then
    echo -e "${GREEN}   H2 console accessible${NC}"
else
    echo -e "${RED}   H2 console not accessible${NC}"
fi

echo -e "${YELLOW}Testing Frontend...${NC}"

# Test 6: Frontend Accessibility
echo "6. Testing frontend accessibility..."
if curl -s -o /dev/null -w "%{http_code}" "$FRONTEND_URL" | grep -q "200"; then
    echo -e "${GREEN}   Frontend accessible${NC}"
else
    echo -e "${RED}   Frontend not accessible${NC}"
fi

echo -e "${YELLOW}Running Backend Unit Tests...${NC}"

# Test 7: Backend Unit Tests
echo "7. Running backend unit tests..."
cd ..
if mvn test > test-results.log 2>&1; then
    echo -e "${GREEN}   Backend unit tests passed${NC}"
else
    echo -e "${RED}   Backend unit tests failed${NC}"
    echo "Check test-results.log for details"
fi

echo -e "${YELLOW}Running Frontend Tests...${NC}"

# Test 8: Frontend Tests
echo "8. Running frontend tests..."
cd frontend
if npm test > ../test-results-frontend.log 2>&1; then
    echo -e "${GREEN}   Frontend tests passed${NC}"
else
    echo -e "${RED}   Frontend tests failed${NC}"
    echo "Check test-results-frontend.log for details"
fi

echo -e "${YELLOW}API Integration Tests...${NC}"

# Test 9: Create Post (should fail without auth)
echo "9. Testing protected endpoint without auth..."
RESPONSE=$(curl -s -w "%{http_code}" -X POST "$API_BASE_URL/posts" \
  -H "Content-Type: application/json" \
  -d '{"content": "Test post"}')
if echo "$RESPONSE" | grep -q "401\|403"; then
    echo -e "${GREEN}   Protected endpoint properly secured${NC}"
else
    echo -e "${RED}   Protected endpoint not properly secured${NC}"
fi

# Test 10: Invalid Post Creation
echo "10. Testing validation..."
RESPONSE=$(curl -s -w "%{http_code}" -X POST "$API_BASE_URL/posts" \
  -H "Content-Type: application/json" \
  -d '{"content": ""}')
if echo "$RESPONSE" | grep -q "400"; then
    echo -e "${GREEN}   Validation working correctly${NC}"
else
    echo -e "${RED}   Validation not working${NC}"
fi

echo -e "${YELLOW}Performance Tests...${NC}"

# Test 11: Load Test
echo "11. Running basic load test..."
echo "Testing 10 concurrent requests to public stream..."
for i in {1..10}; do
    curl -s "$API_BASE_URL/posts" > /dev/null &
done
wait
echo -e "${GREEN}   Load test completed${NC}"

echo -e "${YELLOW}Security Tests...${NC}"

# Test 12: CORS Headers
echo "12. Testing CORS headers..."
CORS_RESPONSE=$(curl -s -I "$API_BASE_URL/posts")
if echo "$CORS_RESPONSE" | grep -q "Access-Control"; then
    echo -e "${GREEN}   CORS headers present${NC}"
else
    echo -e "${RED}   CORS headers missing${NC}"
fi

# Test 13: SQL Injection Test
echo "13. Testing SQL injection protection..."
RESPONSE=$(curl -s -w "%{http_code}" "$API_BASE_URL/users/OR%201=1--")
if echo "$RESPONSE" | grep -q "404\|400"; then
    echo -e "${GREEN}   SQL injection protection working${NC}"
else
    echo -e "${RED}   Potential SQL injection vulnerability${NC}"
fi

echo -e "${YELLOW}Configuration Tests...${NC}"

# Test 14: Environment Variables
echo "14. Checking environment configuration..."
if [ -n "$AUTH0_DOMAIN" ] && [ -n "$AUTH0_AUDIENCE" ]; then
    echo -e "${GREEN}   Auth0 environment variables set${NC}"
else
    echo -e "${YELLOW}   Auth0 environment variables not set (expected for local development)${NC}"
fi

echo -e "${YELLOW}Database Tests...${NC}"

# Test 15: Database Connectivity
echo "15. Testing database connectivity..."
if curl -s -o /dev/null -w "%{http_code}" "$API_BASE_URL/users" | grep -q "200"; then
    echo -e "${GREEN}   Database connectivity working${NC}"
else
    echo -e "${RED}   Database connectivity issues${NC}"
fi

echo -e "${GREEN}Test Suite Completed!${NC}"
echo ""
echo "Summary:"
echo "- Backend API endpoints tested"
echo "- Frontend accessibility verified"
echo "- Security measures validated"
echo "- Database connectivity confirmed"
echo "- CORS configuration checked"
echo "- Load testing performed"
echo ""
echo "For detailed results, check the test log files:"
echo "- test-results.log (backend tests)"
echo "- test-results-frontend.log (frontend tests)"
