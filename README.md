# Email Server with Bloom Filter Security

A comprehensive email server system with integrated Bloom Filter security to detect and block malicious content in emails.

## 📋 Project Overview

This system consists of two main servers:

1. **Internet Server** (Node.js) - Main email server handling user management and email operations
2. **Bloom Filter Server** (C++) - Security filtering server for detecting malicious URLs

The system automatically checks all URLs in email content against a blacklist and rejects emails containing dangerous links.

## 🏗️ Project Structure

```
EX1/
├── Internet_server/           # Node.js Email Server
│   ├── src/
│   │   ├── Controllers/       # Business logic controllers
│   │   ├── Models/           # Data models
│   │   ├── Routes/           # API route definitions
│   │   ├── services/         # Helper services
│   │   ├── app.js           # Express app configuration
│   │   └── server.js        # Entry point
│   ├── docker/
│   │   └── Dockerfile       # Docker configuration
│   └── package.json
├── Bloom_Filter_Server/      # C++ Security Server
│   ├── src/                 # Source files
│   ├── build/               # Compiled binaries
│   |   └── data/
│   │       └── blacklist.txt    # Malicious URLs blacklist
│   └── Dockerfile
├── docker-compose.yml       # Multi-container orchestration
└── README.md
```

## 🚀 Quick Start

### Prerequisites
- Docker
- Docker Compose

### Running the System
```bash
# Clone the repository
git clone <repository-url>
cd EX1

# Start the entire system
docker-compose up --build
```

### Background Execution
```bash
docker-compose up --build -d
```

### Stop the System
```bash
docker-compose down
```

## 🔧 API Documentation

### Authentication
All requests(besides user creation and login) require header: `user-id: <number>`

### Users API

#### Create New User
```bash
POST /api/users
Content-Type: application/json

{
  "username": "alice",
  "password": "123",
  "first_name": "Alice",
  "sur_name": "Smith",
  "picture": ""
}
```

#### User Login
```bash
POST /api/users/login
Content-Type: application/json

{
  "username": "alice",
  "password": "123"
}
```

### Emails API

#### Send Email
```bash
POST /api/mails
Content-Type: application/json
user-id: 1

{
  "receiver": "bob",
  "subject": "Hello",
  "body": "This is a test email"
}
```

#### Get All Emails
```bash
GET /api/mails
user-id: 1
```

#### Get Specific Email
```bash
GET /api/mails/:id
user-id: 1
```

#### Delete Email
```bash
DELETE /api/mails/:id
user-id: 1
```

#### Search Emails
```bash
GET /api/mails/search?q=searchTerm
user-id: 1
```

#### Update Email
```bash
PATCH /api/mails/:id
Content-Type: application/json
user-id: 1

{
  "subject": "Updated Subject",
  "body": "Updated body content"
}
```

### Labels API

#### Create Label
```bash
POST /api/labels
Content-Type: application/json
user-id: 1

{
  "labelName": "Important"
}
```

#### Get All Labels
```bash
GET /api/labels
user-id: 1
```

#### Get Specific Label
```bash
GET /api/labels/:id
user-id: 1
```

#### Update Label
```bash
PUT /api/labels/:id
Content-Type: application/json
user-id: 1

{
  "newName": "Very Important"
}
```

#### Delete Label
```bash
DELETE /api/labels/:id
user-id: 1
```

## 🛡️ Security System (Bloom Filter)

### How It Works
1. When an email is sent, the system scans the content for URLs
2. Each URL is checked against the Bloom Filter server
3. If the URL is found in the blacklist, the email is rejected
4. Only "safe" emails are delivered to recipients

### Adding URLs to Blacklist
Edit the file:
```
Bloom_Filter_Server/build/data/blacklist.txt
```
Add each malicious URL on a separate line.

### Example Blacklist Entry
```
http://malicious.com
https://dangerous-site.net
http://phishing-example.org
```

## 🧪 Testing Examples

### Creating Users
```bash
# Create Alice
curl -X POST http://localhost:8080/api/users \
-H "Content-Type: application/json" \
-d '{"username": "alice", "password": "123", "first_name": "Alice", "sur_name": "Smith", "picture": ""}'

# Create Bob
curl -X POST http://localhost:8080/api/users \
-H "Content-Type: application/json" \
-d '{"username": "bob", "password": "456", "first_name": "Bob", "sur_name": "Jones", "picture": ""}'
```

### Sending Safe Email
```bash
curl -X POST http://localhost:8080/api/mails \
-H "Content-Type: application/json" \
-H "user-id: 1" \
-d '{
  "receiver": "bob",
  "subject": "Safe Email",
  "body": "This is a safe email without dangerous links"
}'
```

### Testing Malicious Email (Will be Rejected)
```bash
curl -X POST http://localhost:8080/api/mails \
-H "Content-Type: application/json" \
-H "user-id: 1" \
-d '{
  "receiver": "bob",
  "subject": "Dangerous",
  "body": "Don'\''t click http://malicious.com"
}'
```

### Searching Emails
```bash
curl -X GET "http://localhost:8080/api/mails/search?q=Safe" \
-H "user-id: 1"
```

### Working with Labels
```bash
# Create a label
curl -X POST http://localhost:8080/api/labels \
-H "Content-Type: application/json" \
-H "user-id: 1" \
-d '{"labelName": "Work"}'

# Get all labels
curl -X GET http://localhost:8080/api/labels \
-H "user-id: 1"

# Update label
curl -X PUT http://localhost:8080/api/labels/Work \
-H "Content-Type: application/json" \
-H "user-id: 1" \
-d '{"newName": "Business"}'
```

## 🐛 Troubleshooting

### Container Won't Start
```bash
# Check logs
docker-compose logs -f

# Check container status
docker-compose ps

# Rebuild without cache
docker-compose build --no-cache
```

### Connection Errors
```bash
# Check if ports are available
netstat -tulpn | grep :8080
netstat -tulpn | grep :4000

# Restart with fresh build
docker-compose down
docker-compose up --build
```

### Permission Issues
```bash
# Fix file permissions
chmod +x Bloom_Filter_Server/build/bloom_filter_app
```

## 🔧 Advanced Configuration

### Changing Ports
Edit `docker-compose.yml`:
```yaml
services:
  web-server:
    ports:
      - "3000:8080"  # external:internal
  bloom-server:
    ports:
      - "5000:4000"  # external:internal
```

### Environment Variables
```yaml
services:
  web-server:
    environment:
      - NODE_ENV=development
      - DEBUG=true
      - PORT=8080
```

### Custom Blacklist Location
Modify the Bloom Filter server to use a custom blacklist file location.

## 📊 Data Structures

### User Object
```json
{
  "id": 1,
  "username": "alice",
  "first_name": "Alice",
  "sur_name": "Smith",
  "picture": "",
  "inbox": [],
  "labels": {}
}
```

### Email Object
```json
{
  "id": 1,
  "sender": "alice",
  "receiver": "bob",
  "subject": "Hello World",
  "body": "This is a test email",
  "dateCreated": "2025-06-30T12:00:00.000Z"
}
```

### Label Object
```json
{
  "name": "Important",
  "color": "#ff0000",
  "description": "High priority emails"
}
```

## 🔒 Security Features

- **URL Filtering**: Automatic detection and blocking of malicious URLs
- **Bloom Filter**: Efficient probabilistic data structure for blacklist checking
- **Input Validation**: Server-side validation of all inputs
- **Error Handling**: Comprehensive error handling and logging

## 🚀 Performance Considerations

- **Bloom Filter**: O(1) average time complexity for URL lookups
- **Memory Efficient**: Minimal memory footprint for blacklist storage
- **Containerized**: Isolated processes for better resource management
- **Asynchronous Processing**: Non-blocking I/O operations

## 📈 Monitoring and Logging

### Check Server Health
```bash
# Web server health
curl http://localhost:8080/api/users

# View logs
docker-compose logs web-server
docker-compose logs bloom-server
```

### Performance Monitoring
```bash
# Container resource usage
docker stats

# Container processes
docker-compose top
```

## 🔄 Development Workflow

### Local Development
```bash
# Start in development mode
NODE_ENV=development docker-compose up

# Watch for changes
docker-compose up --build
```

### Testing
```bash
# Run API tests
npm test

# Integration tests
docker-compose -f docker-compose.test.yml up
```

## 📋 API Response Codes

- `200 OK` - Successful GET/PUT/PATCH
- `201 Created` - Successful POST
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Invalid input/malicious content
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## 🎯 Future Enhancements

- [ ] Database persistence (PostgreSQL/MongoDB)
- [ ] JWT authentication
- [ ] Real-time notifications
- [ ] Email attachments support
- [ ] Advanced search filters
- [ ] Email templates
- [ ] Spam detection algorithms
- [ ] Rate limiting

## 👥 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`) 
5. Open a Pull Request

## 📄 License

This project is part of the Advanced Programming course at Bar-Ilan University.

## 📞 Support

For questions or issues, please contact: [your-email@example.com]

---

**Note:** Ensure ports 8080 and 4000 are available on your system before running the application.

**System Requirements:**
- Docker version 20.0+
- Docker Compose version 2.0+
- Minimum 2GB RAM
- 1GB free disk space