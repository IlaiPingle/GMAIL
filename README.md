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

### Stop the System

```bash
docker-compose down
```
or: `ctrl+c` at the same terminal

## Usage Examples

### Create two Users

```bash
curl curl -i -X POST http://localhost:8080/api/users -H 'Content-Type: application/json' -d '{
  "username": "alice",
  "password": "1234",
  "first_name": "Alice",
  "sur_name": "Wonder",
  "picture": "https://example.com/alice.jpg"
}'

curl -i -X POST http://localhost:8080/api/users -H 'Content-Type: application/json' -d '{
  "username": "bob",
  "password": "abcd",
  "first_name": "Bob",
  "sur_name": "Builder",
  "picture": "https://example.com/bob.jpg"
}'
```

### 🔐 Login 

```bash
curl -i -X POST http://localhost:8080/api/tokens -H 'Content-Type: application/json' -d '{"username": "alice", "password": "1234"}'
```

### 📄 Get User's information

```bash
curl -X GET http://localhost:8080/api/users/1
```

### 📬 Send Email 

```bash
curl -i -X POST http://localhost:8080/api/mails -H 'Content-Type: application/json' -H 'user-id: 1' -d '{
  "sender": "alice",
  "receiver": "bob",
  "subject": "Meeting",
  "body": "Can we meet tomorrow at 10?"
}'
```

### 📥 Get last 50 Emails

```bash
curl -i http://localhost:8080/api/mails -H 'user-id: 1'
```

### 📄 Get Email by ID

```bash
curl -i http://localhost:8080/api/mails/1 -H 'user-id: 1'
```

### 🔍 Search Emails

```bash
curl -i http://localhost:8080/api/mails/search?q=meeting -H 'user-id: 1'
```

### 📝 Update Email

```bash
curl -X PATCH http://localhost:8080/api/mails/1 \
-H "Content-Type: application/json" \
-H "user-id: 1" \
-d '{
  "subject": "Updated Subject",
  "body": "Updated body"
}'
```

### 🗑️ Delete Email

```bash
curl -X DELETE http://localhost:8080/api/mails/1 \
-H "user-id: 1"
```

### 🏷️ Create Label

```bash
curl -i -X POST http://localhost:8080/api/labels -H 'Content-Type: application/json' -H 'user-id: 1' -d '{"labelName": "Work"}'
```

### 🏷️ Get All Labels

```bash
curl -i http://localhost:8080/api/labels -H 'user-id: 1'
```

### 🏷️ Get Label by Name

```bash
curl -i -X GET http://localhost:8080/api/labels/Work -H 'user-id: 1'
```

### 🏷️ Update Label

```bash
curl -i -X PATCH http://localhost:8080/api/labels/Work -H 'Content-Type: application/json' -H 'user-id: 1' -d '{"newName": "Work-Updated"}'
```

### 🗑️ Delete Label

```bash
curl -i -X DELETE http://localhost:8080/api/labels/Work-Updated -H 'user-id: 1'
```

### 🚫 Add URL to Blacklist

```bash
curl -i -X POST http://localhost:8080/api/blacklist -H 'Content-Type: application/json' -H 'user-id: 1' -d '{"url": "http://malicious.com"}'
```

### 🗑️ Remove URL from Blacklist

```bash
curl -i -X DELETE "http://localhost:8080/api/blacklist/http%3A%2F%2Fmalicious.com" -H 'user-id: 1
```
