# Quick Start (Docker)

## 1. Clone
```
git clone <repo-url>
cd EX1
```

## 2. Environment (backend/.env)
```
MONGODB_URI=mongodb://mongodb:27017/maildb
PORT=8080
JWT_SECRET=dev-secret
BLOOM_SERVER_HOST=bloom-server
BLOOM_SERVER_PORT=4000
UPLOAD_DIR=/app/uploads
```

## 3. Build & Run
```
docker compose up --build
```

### Services (docker-compose)
| Service        | Purpose                | Host Port |
|----------------|------------------------|-----------|
| web-server     | Node.js backend        | 8080      |
| frontend       | React (nginx)          | 3000      |
| bloom-server   | C++ Bloom filter       | 4000      |
| mongodb        | MongoDB database       | 27017     |

- Backend API: http://localhost:8080
- Frontend UI: http://localhost:3000
- Bloom Filter: internal (bloom-server:4000)
- MongoDB: mongodb:27017 (or localhost:27017 from host)

## 4. Health / Smoke Tests
```
curl http://localhost:8080/users/me   # (after login)
curl -X POST http://localhost:8080/blacklist -d "{\"url\":\"http://example.com\"}" -H "Content-Type: application/json" -b cookie.txt
```

## 5. Logs
```
docker compose logs -f web-server
docker compose logs -f bloom-server
```

## 6. Rebuild a Single Service
```
docker compose build web-server && docker compose up -d web-server
```

## 7. Stop
```
docker compose down
```

Fresh DB (drop volume):
```
docker compose down -v
```

## 8. Common Issues
- Port already in use → change published port in compose.
- Backend can’t reach bloom-server → confirm BLOOM_SERVER_HOST matches service name.
- Android cannot connect → use http://10.0.2.2:8080 (not localhost).

## 9. Compose File Notes
- Uploads are bind-mounted (backend/uploads).
- Mongo data persisted via named volume (mongo_data).
- All services share default bridge network; service names resolve via DNS.