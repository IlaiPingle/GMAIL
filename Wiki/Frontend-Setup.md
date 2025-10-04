# Frontend (React)

## Install
```
cd frontend
npm install
```

## Development
```
npm start
```
Runs on http://localhost:3000

## Production (manual)
```
npm run build
serve -s build
```

## Docker Build
Handled in docker/dockerfile:
- Build stage (Node 20)
- Nginx stage (serves build/)

## API Base
If needed, create .env:
```
REACT_APP_API_BASE=http://localhost:8080/api
```
Client.js should read the same base or default to /api relative path.