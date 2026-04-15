@echo off
REM AWS Deployment Script for TDSE Twitter-like Application (Windows)

setlocal enabledelayedexpansion

echo Starting AWS deployment...

REM Configuration
set AWS_REGION=us-east-1
set S3_BUCKET=tdse-twitter-frontend-%random%
set STACK_NAME=tdse-twitter-stack

REM Build and deploy frontend to S3
echo Building frontend...
cd frontend
call npm install
call npm run build

echo Creating S3 bucket...
aws s3 mb s3://%S3_BUCKET% --region %AWS_REGION%

echo Uploading frontend to S3...
aws s3 sync build/ s3://%S3_BUCKET% --delete

echo Configuring S3 bucket for static website hosting...
aws s3 website s3://%S3_BUCKET% --index-document index.html --error-document index.html

echo Setting S3 bucket policy for public access...
echo { > bucket-policy.json
echo   "Version": "2012-10-17", >> bucket-policy.json
echo   "Statement": [ >> bucket-policy.json
echo     { >> bucket-policy.json
echo       "Sid": "PublicReadGetObject", >> bucket-policy.json
echo       "Effect": "Allow", >> bucket-policy.json
echo       "Principal": "*", >> bucket-policy.json
echo       "Action": "s3:GetObject", >> bucket-policy.json
echo       "Resource": "arn:aws:s3:::%S3_BUCKET%/*" >> bucket-policy.json
echo     } >> bucket-policy.json
echo   ] >> bucket-policy.json
echo } >> bucket-policy.json

aws s3api put-bucket-policy --bucket %S3_BUCKET% --policy file://bucket-policy.json

cd ..

REM Deploy microservices using Serverless Framework
echo Deploying microservices...

for %%s in (user-service post-service stream-service) do (
    echo Deploying %%s...
    cd microservices\%%s
    
    REM Install serverless framework if not present
    where serverless >nul 2>nul
    if errorlevel 1 (
        echo Installing Serverless Framework...
        npm install -g serverless
    )
    
    REM Deploy
    serverless deploy
    
    cd ..\..
)

echo Deployment completed!
echo Frontend URL: http://%S3_BUCKET%.s3-website-%AWS_REGION%.amazonaws.com
echo API endpoints will be displayed above for each microservice

REM Cleanup
del bucket-policy.json

pause
