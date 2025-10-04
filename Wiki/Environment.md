# Environment Variables

The backend reads from backend/.env (loaded into the container via docker-compose).

Required:
```
MONGODB_URI=mongodb://mongodb:27017/maildb
PORT=8080
JWT_SECRET=change-me
BLOOM_SERVER_HOST=bloom-server
BLOOM_SERVER_PORT=4000
UPLOAD_DIR=/app/uploads
```

Optional:
```
LOG_LEVEL=info
COOKIE_NAME=token
```

Android uses:
- Base URL: http://10.0.2.2:8080

Frontend (optional .env):
```
REACT_APP_API_BASE=http://localhost:8080
```