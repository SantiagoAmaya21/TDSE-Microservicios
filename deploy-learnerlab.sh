#!/bin/bash

# TDSE Microservices - AWS Learner Lab Deployment Script
# Compatible with AWS Academy Learner Lab restrictions

set -e

echo "=== TDSE Microservices - Learner Lab Deployment ==="
echo "Region: us-east-1"
echo "Using pre-created LabRole (no IAM creation)"
echo ""

# Configuration
STACK_NAME="tdse-microservices-$(date +%s)"
REGION="us-east-1"
AUTH0_ISSUER="${AUTH0_ISSUER:-https://your-domain.auth0.com}"
AUTH0_AUDIENCE="${AUTH0_AUDIENCE:-https://your-domain.auth0.com/api/v2/}"

echo "Stack Name: $STACK_NAME"
echo "Auth0 Issuer: $AUTH0_ISSUER"
echo "Auth0 Audience: $AUTH0_AUDIENCE"
echo ""

# Check if AWS CLI is available
if ! command -v aws &> /dev/null; then
    echo "ERROR: AWS CLI is not installed or not in PATH"
    exit 1
fi

# Check if SAM CLI is available
if ! command -v sam &> /dev/null; then
    echo "ERROR: SAM CLI is not installed or not in PATH"
    echo "Install SAM CLI: pip install aws-sam-cli"
    exit 1
fi

# Verify we're in the correct directory
if [ ! -f "template.yaml" ]; then
    echo "ERROR: template.yaml not found in current directory"
    echo "Please run this script from the project root directory"
    exit 1
fi

echo "=== Building SAM Application ==="
sam build --region $REGION

echo ""
echo "=== Deploying SAM Application ==="
echo "This will use the pre-created LabRole (no IAM creation needed)"

sam deploy \
    --stack-name $STACK_NAME \
    --region $REGION \
    --no-confirm-changeset \
    --no-fail-on-empty-changeset \
    --capabilities CAPABILITY_AUTO_EXPAND \
    --parameter-overrides \
        Auth0Issuer=$AUTH0_ISSUER \
        Auth0Audience=$AUTH0_AUDIENCE \
    --no-iam-capabilities

echo ""
echo "=== Deployment Complete ==="
echo "Stack Name: $STACK_NAME"
echo "Region: $REGION"

# Get the API Gateway endpoint
API_ENDPOINT=$(aws cloudformation describe-stacks \
    --stack-name $STACK_NAME \
    --region $REGION \
    --query 'Stacks[0].Outputs[?OutputKey==`ApiEndpoint`].OutputValue' \
    --output text)

echo "API Gateway Endpoint: $API_ENDPOINT"
echo ""
echo "=== API Endpoints ==="
echo "User Service: $API_ENDPOINT/api/users"
echo "Post Service: $API_ENDPOINT/api/posts"
echo "Stream Service: $API_ENDPOINT/api/stream"
echo ""
echo "=== Cleanup Command ==="
echo "To delete the stack when done:"
echo "aws cloudformation delete-stack --stack-name $STACK_NAME --region $REGION"
echo ""
echo "=== Test the Deployment ==="
echo "Test health endpoint:"
echo "curl -H 'Authorization: Bearer YOUR_JWT_TOKEN' $API_ENDPOINT/api/users/me"
echo ""

# Save stack name for cleanup
echo $STACK_NAME > .stack-name
echo "Stack name saved to .stack-name file"
