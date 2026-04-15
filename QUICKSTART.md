# TDSE Twitter Application - Quick Start Guide

This guide will help you get the TDSE Twitter-like application running in minutes.

## Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+
- Auth0 account (free tier is sufficient)

## Step 1: Auth0 Setup (5 minutes)

1. **Create Auth0 Account**
   - Go to https://auth0.com and sign up
   - Create a new tenant

2. **Create SPA Application**
   - Dashboard > Applications > Applications > Create Application
   - Choose "Single Page Web Applications"
   - Name: "TDSE Twitter Frontend"
   - Save the **Client ID** and **Domain**

3. **Create API Application**
   - Dashboard > Applications > APIs > Create API
   - Name: "TDSE Twitter API"
   - Identifier: "https://tdse-twitter-api.com" (or your custom identifier)
   - Save the **Identifier** (this is your Audience)

4. **Configure Allowed URLs**
   - In your SPA application settings:
   - Allowed Callback URLs: `http://localhost:3000`
   - Allowed Logout URLs: `http://localhost:3000`
   - Allowed Web Origins: `http://localhost:3000`

## Step 2: Backend Setup (2 minutes)

1. **Configure Auth0 in Backend**
   ```bash
   cp application.properties.example src/main/resources/application.properties
   ```
   
2. **Edit application.properties**
   ```properties
   auth0.domain=YOUR_AUTH0_DOMAIN.auth0.com
   auth0.audience=YOUR_API_IDENTIFIER
   ```

3. **Start Backend**
   ```bash
   mvn spring-boot:run
   ```

## Step 3: Frontend Setup (2 minutes)

1. **Navigate to Frontend**
   ```bash
   cd frontend
   ```

2. **Install Dependencies**
   ```bash
   npm install
   ```

3. **Configure Environment**
   ```bash
   cp .env.example .env.local
   ```
   
4. **Edit .env.local**
   ```env
   REACT_APP_AUTH0_DOMAIN=YOUR_AUTH0_DOMAIN.auth0.com
   REACT_APP_AUTH0_CLIENT_ID=YOUR_CLIENT_ID
   REACT_APP_AUTH0_AUDIENCE=YOUR_API_IDENTIFIER
   REACT_APP_API_URL=http://localhost:8080/api
   ```

5. **Start Frontend**
   ```bash
   npm start
   ```

## Step 4: Test the Application (1 minute)

1. **Open Browser**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

2. **Test Authentication**
   - Click "Login" button
   - You'll be redirected to Auth0
   - Login with any method (Google, GitHub, etc.)
   - You'll be redirected back to the app

3. **Create a Post**
   - Once logged in, type a message (max 140 characters)
   - Click "Post" button
   - Your post should appear in the stream

## Step 5: Verify Everything Works

### Check These Endpoints:
- `http://localhost:8080/api/posts` - Public stream (should work without auth)
- `http://localhost:8080/api/users/me` - Current user (requires auth)
- `http://localhost:8080/swagger-ui.html` - API documentation

### Test These Features:
- [ ] Login/Logout works
- [ ] Can create posts (max 140 chars)
- [ ] Posts appear in public stream
- [ ] User profile information displays
- [ ] API endpoints are properly secured

## Troubleshooting

### Common Issues:

**"Auth0 domain is incorrect"**
- Check your Auth0 domain in application.properties
- Make sure you're using the full domain (e.g., `your-tenant.auth0.com`)

**"Frontend can't connect to backend"**
- Ensure backend is running on port 8080
- Check CORS configuration
- Verify API_URL in .env.local

**"JWT validation failed"**
- Check Auth0 audience configuration
- Ensure API identifier matches between frontend and backend
- Verify Auth0 application permissions

**"H2 console not accessible"**
- Check `spring.h2.console.enabled=true` in application.properties
- Access at: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: `password`

## Next Steps

Once everything is working locally:

1. **Read the full README.md** for detailed documentation
2. **Run the test suite**: `deployment/test.sh` or `deployment\test.bat`
3. **Deploy to AWS**: Follow the deployment guide in README.md
4. **Explore the API**: Use Swagger UI to test all endpoints

## Need Help?

- Check the full documentation in `README.md`
- Review the API documentation at `/swagger-ui.html`
- Examine the code structure in the `src/` directories
- Check the test files for usage examples

**Enjoy your TDSE Twitter-like application!**
