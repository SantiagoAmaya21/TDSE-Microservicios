#!/bin/bash

# TDSE Microservices - AWS Learner Lab Cleanup Script
# Safely delete all resources to avoid budget overruns

set -e

echo "=== TDSE Microservices - Learner Lab Cleanup ==="

# Get stack name from file or prompt
if [ -f ".stack-name" ]; then
    STACK_NAME=$(cat .stack-name)
    echo "Found stack name: $STACK_NAME"
else
    read -p "Enter stack name to delete: " STACK_NAME
fi

REGION="us-east-1"

echo "Stack Name: $STACK_NAME"
echo "Region: $REGION"
echo ""

# Confirm deletion
read -p "Are you sure you want to delete stack '$STACK_NAME'? (y/N): " confirm
if [[ $confirm != [yY] ]]; then
    echo "Cleanup cancelled"
    exit 0
fi

echo "=== Deleting CloudFormation Stack ==="
aws cloudformation delete-stack \
    --stack-name $STACK_NAME \
    --region $REGION

echo "Waiting for stack deletion to complete..."
aws cloudformation wait stack-delete-complete \
    --stack-name $STACK_NAME \
    --region $REGION

echo "=== Stack Deleted Successfully ==="

# Clean up local files
rm -f .stack-name

echo "=== Cleanup Complete ==="
echo "All AWS resources have been deleted"
echo "Budget is now preserved"
