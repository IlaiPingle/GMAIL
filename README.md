# Email Server with Bloom Filter Security

## Overview

This project is a simple email server system with integrated URL security filtering. It consists of two main components:

1. **Internet Server (Node.js)** - Handles user management, emails, and labels
2. **Bloom Filter Server (C++)** - Security service that filters malicious URLs

The system allows users to register, send emails, manage labels, and automatically checks email content for malicious URLs before delivery.

## Project Structure

```
EX1/
├── Internet_server/     # Node.js email server
├── Bloom_Filter_Server/ # C++ security filter
└── docker-compose.yml   # Container orchestration
```

## Running the Program

### Prerequisites
- Docker
- Docker Compose

### Start the System

```bash
# Start both servers
docker-compose up --build
```

This will:
- Build and start both the Internet Server and Bloom Filter Server
- Make the email server available on port 8080
- Make the security filter available on port 4000

### Run in Background

```bash
docker-compose up --build -d
```


### Stop the System

```bash
docker-compose down
```

## Usage Examples

### Create a User

```bash
curl -X POST http://localhost:8080/api/users \
-H "Content-Type: application/json" \
-d '{
  "username": "user1", 
  "password": "123", 
  "first_name": "Test", 
  "sur_name": "User", 
  "picture": ""
}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/tokens \
-H "Content-Type: application/json" \
-d '{
  "username": "user1",
  "password": "123"
}'
```

### Send Email

```bash
curl -X POST http://localhost:8080/api/mails \
-H "Content-Type: application/json" \
-H "user-id: 1" \
-d '{
  "receiver": "user2",
  "subject": "Hello",
  "body": "This is a test email"
}'
```

### Create Label

```bash
curl -X POST http://localhost:8080/api/labels \
-H "Content-Type: application/json" \
-H "user-id: 1" \
-d '{
  "lableName": "Important"
}'
```

### Get All Users

```bash
curl -X GET http://localhost:8080/api/users/1
```

### Search Emails

```bash
curl -X GET "http://localhost:8080/api/mails/search?query=Hello" \
-H "user-id: 1"
```

### Get All Emails

```bash
curl -X GET http://localhost:8080/api/mails \
-H "user-id: 1"
```

### Get Email by ID

```bash
curl -X GET http://localhost:8080/api/mails/1 \
-H "user-id: 1"
```

### Update Email

```bash
curl -X PATCH http://localhost:8080/api/mails/1 \
-H "Content-Type: application/json" \
-H "user-id: 1" \
-d '{
  "subject": "Updated Subject",
  "body": "Updated body"
}'
```

### Delete Email

```bash
curl -X DELETE http://localhost:8080/api/mails/1 \
-H "user-id: 1"
```

### Get All Labels

```bash
curl -X GET http://localhost:8080/api/labels \
-H "user-id: 1"
```

### Get Label by Name

```bash
curl -X GET http://localhost:8080/api/labels/Important \
-H "user-id: 1"
```

### Update Label

```bash
curl -X PUT http://localhost:8080/api/labels/Important \
-H "Content-Type: application/json" \
-H "user-id: 1" \
-d '{
  "newName": "Work"
}'
```

### Delete Label

```bash
curl -X DELETE http://localhost:8080/api/labels/Work \
-H "user-id: 1"
```

### Add URL to Blacklist

```bash
curl -X POST http://localhost:8080/api/blacklist \
-H "Content-Type: application/json" \
-d '{
  "url": "http://malicious.com"
}'
```

### Remove URL from Blacklist

```bash
curl -X DELETE http://localhost:8080/api/blacklist/1
```
