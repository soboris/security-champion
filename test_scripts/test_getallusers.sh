#!/bin/bash

# Admin credentials
USERNAME="admin"
PASSWORD="admin123"
LOGIN_URL="http://localhost:8080/api/users/login"
GET_USERS_URL="http://localhost:8080/api/users/getallusers"

# Check if jq is installed
if ! command -v jq &> /dev/null
then
    echo "The jq tool is required but it's not installed. Please install jq (https://stedolan.github.io/jq/) and try again."
    exit 1
fi

# Step 1: Log in as Admin to get JWT token
echo "Logging in as $USERNAME to get JWT token..."
RESPONSE=$(curl -s -X POST $LOGIN_URL \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=$USERNAME&password=$PASSWORD")

# Extract JWT token from the response using jq
TOKEN=$(echo $RESPONSE | jq -r '.token')

# Check if token was successfully retrieved
if [ "$TOKEN" == "null" ] || [ -z "$TOKEN" ]; then
  echo "Failed to log in and retrieve token. Response: $RESPONSE"
  exit 1
fi

echo "Token retrieved: $TOKEN"

# Step 2: Use the JWT token to get all users
echo "Fetching all users..."
curl -X GET $GET_USERS_URL \
  -H "Authorization: Bearer $TOKEN"
