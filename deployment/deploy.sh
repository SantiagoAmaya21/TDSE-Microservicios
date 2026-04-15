#!/bin/bash

# AWS Deployment Script for TDSE Twitter-like Application

set -e

echo "Starting AWS deployment..."

# Configuration
AWS_REGION="us-east-1"
S3_BUCKET="tdse-twitter-frontend-$(date +%s)"
STACK_NAME="tdse-twitter-stack"

# Build and deploy frontend to S3
echo "Building frontend..."
cd frontend
npm install
npm run build

echo "Creating S3 bucket..."
aws s3 mb s3://$S3_BUCKET --region $AWS_REGION

echo "Uploading frontend to S3..."
aws s3 sync build/ s3://$S3_BUCKET --delete

echo "Configuring S3 bucket for static website hosting..."
aws s3 website s3://$S3_BUCKET --index-document index.html --error-document index.html

echo "Setting S3 bucket policy for public access..."
cat > bucket-policy.json << EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::$S3_BUCKET/*"
        }
    ]
}
EOF

aws s3api put-bucket-policy --bucket $S3_BUCKET --policy file://bucket-policy.json

cd ..

# Deploy microservices using Serverless Framework
echo "Deploying microservices..."

for service in user-service post-service stream-service; do
    echo "Deploying $service..."
    cd microservices/$service
    
    # Create pom.xml if it doesn't exist
    if [ ! -f "pom.xml" ]; then
        cat > pom.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.tdse.twitter</groupId>
    <artifactId>$service</artifactId>
    <version>1.0.0</version>
    <name>TDSE Twitter $service</name>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>com.amazonaws.serverless</groupId>
            <artifactId>aws-serverless-java-container-springboot3</artifactId>
            <version>2.0.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
EOF
    fi
    
    # Build and deploy
    mvn clean package -DskipTests
    
    # Install serverless framework if not present
    if ! command -v serverless &> /dev/null; then
        echo "Installing Serverless Framework..."
        npm install -g serverless
    fi
    
    serverless deploy
    
    cd ../..
done

echo "Deployment completed!"
echo "Frontend URL: http://$S3_BUCKET.s3-website-$AWS_REGION.amazonaws.com"
echo "API endpoints will be displayed above for each microservice"

# Cleanup
rm -f bucket-policy.json
