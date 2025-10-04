# Project Overview

A multi-platform mail system:
- C++ Bloom Filter microservice (URL blacklist) — ./Bloom_Filter_Server
- Node.js backend REST API — ./backend
- React web client — ./frontend
- Android native client — ./Androidproject
- MongoDB (Docker)
- Docker Compose orchestration

## Key Features
- User auth (cookie-based)
- Email CRUD + labels (system + user-defined)
- Spam + bin handling (label-based)
- URL blacklist via Bloom Filter service
- Multi-client sync (web + Android)

## Start Here
1. [Prerequisites](Prerequisites.md)
2. [Quick Start with Docker](QuickStart-Docker.md)
3. [Environment Variables](Environment.md)
4. [Architecture](Services-Architecture.md)
5. [Android Setup](Android-Setup.md)
6. [Troubleshooting](Troubleshooting.md)

## API Reference
See [API Overview](API-Overview.md).
Base Url: http://localhost:8080