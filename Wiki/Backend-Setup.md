# Backend (Manual Run Without Docker)

## Install
```
cd backend
npm install
```

## .env
See [Environment Variables](Environment.md)

## Run
```
npm start
```
Server logs: MongoDB connection + "Server listening on 8080" (from src/server.js).

## Important Files
- src/app.js (middleware + routes)
- src/server.js (bootstraps listen)
- src/Controllers/*.js
- src/services/*.js
- src/Models/emailModel.js, userModel.js
- src/Routes/*.js

## CORS
Configured in app.js:
```
origin: ['http://localhost:3000','http://10.0.2.2:8080']
credentials: true
```
Adjust if deploying elsewhere.

## Uploads
Stored under backend/uploads (mounted as volume in docker-compose).