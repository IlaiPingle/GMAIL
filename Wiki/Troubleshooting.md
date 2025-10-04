# Troubleshooting

## Android ConnectException (/10.0.2.2:8080)
- Backend container not running
- Wrong base URL
- Port not exposed in docker-compose
- Firewall blocking 8080

## 401 Unauthorized
- Missing/expired cookie
- Using different OkHttp client without CookieJar
- CORS origin mismatch

## 400 PATCH /labels/:id
- Body must be: {"newName":"..."}
- Empty or duplicate name rejected

## 404 After Following Old Docs
Docs mentioning /api* while backend has no prefix → remove /api or add prefix in app.js.

## 500 "Updating the path 'labels'"
- Ensure labelService uses aggregation pipeline for rename

## Mail Reappears in Inbox After Moving to Bin
- Backend PATCH /emails/:id not persisting labels
- Needs label update support

## Blacklist Fails
- bloom-server container not healthy
- Wrong BLOOM_SERVER_HOST when running locally (use 127.0.0.1 if not Docker)

## Mongo Connection Error
- Check MONGODB_URI in .env
- Ensure service name is mongodb when using compose

## Frontend Cannot Auth
- Cookie blocked: ensure SameSite and domain defaults
- Use credentials: 'include' in fetch/axios (already in client code)

## Rebuild a Single Service
```
docker compose build web-server && docker compose up -d web-server
```