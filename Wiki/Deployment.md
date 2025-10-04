# Deployment

## Overview
Docker-based deployment bundles:
- Backend (Node.js + Express)
- Frontend (static React build served by nginx)
- Bloom Filter microservice (C++)
- MongoDB

## Production Run
```
docker compose build
docker compose up -d
```

## Environment Hardening
- Use a strong JWT_SECRET (not checked into VCS)
- Set secure & sameSite cookies behind HTTPS
- Restrict CORS origins to known domains
- Add rate limiting + helmet middleware
- Rotate secrets periodically

## Scaling Strategy
| Layer        | Approach                          |
|--------------|-----------------------------------|
| Backend API  | Horizontal scale (stateless)      |
| MongoDB      | Managed cluster / replica set     |
| Bloom Filter | Replace with distributed service  |
| Frontend     | CDN + static object storage       |

## Observability
- Structured logging (winston/pino)
- Health endpoint (/health)
- Central log aggregation
- Container metrics (Docker / Prometheus)

## Backup / Data
- Snapshot Mongo volume (or use managed Atlas backups)
- Externalize uploads (S3, Blob storage) if growth expected

## CI/CD Ideas
- Build & test on push (frontend + backend + Bloom tests)
- Tag images with git SHA
- Deploy via compose or Kubernetes manifests

---

## Build Artifacts / Dockerfiles (Merged from Dockerfiles.md)

### Backend (backend/docker/Dockerfile)
Key steps:
1. Base: node:20
2. Copy package*.json → npm install
3. Copy src + .env (or mount env at runtime)
4. Expose 8080
5. CMD: `node src/server.js`

Multi-stage suggestion (optional):
- Builder stage: install dev deps
- Runtime stage: copy only dist + production deps

### Frontend (frontend/docker/dockerfile)
Multi-stage:
1. Build stage (node:20) → `npm ci && npm run build`
2. Nginx stage (nginx:alpine) → copy build/ to /usr/share/nginx/html
3. Provide custom nginx.conf (rewrite handling)

Environment injection:
- For static builds, bake API base into build or use runtime config pattern.

### Bloom Filter Service (Bloom_Filter_Server/Dockerfile)
1. Base: ubuntu:22.04
2. Install build-essential, cmake
3. Configure & build via CMake
4. Expose 4000
5. CMD runs binary with args (port, capacity, hash ids)

Optimizations:
- Use build stage + slim runtime (copy only binary + required libs)
- Enable strip symbols: `-DCMAKE_BUILD_TYPE=Release && strip <binary>`

### Bloom Tests (Bloom_Filter_Server/Tests/dockerfile)
Builds test target and executes test runner.
Use in CI:
```
docker build -t bloom-tests -f Bloom_Filter_Server/Tests/dockerfile .
docker run --rm bloom-tests
```

### Common Optimization Tips
- Use `npm ci` instead of `npm install` in CI
- Add `.dockerignore` to reduce build context
- Leverage layer caching (separate package install from code copy)
- Pin base image tags to specific digests for reproducibility

---

## Security Checklist
- Run containers as non-root (adjust Dockerfiles)
- Add dependency scanning (npm audit, osv-scanner)
- Limit exposed ports (only 8080, 3000, 4000, 27017 as necessary)
- Use HTTPS upstream (reverse proxy or load balancer)

## Optional: Adding /api Prefix
If you decide to add versioned API (e.g., /api or /v1):
```
app.use('/api', usersRoutes, emailRoutes, labelRoutes, blacklistRoutes);
```
Then update frontend & Android base URLs accordingly.

---

## Rollback Plan
- Keep previous image tags (e.g., `backend:<sha>`)
- On failure: `docker compose down && docker compose up -d backend:<old-tag>`
- Maintain DB snapshots before schema migrations.