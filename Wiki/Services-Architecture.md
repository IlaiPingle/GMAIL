# Architecture

## Overview
- React frontend (nginx container)
- Node.js backend (Express, Mongoose)
- MongoDB (data store)
- Bloom Filter C++ service (URL blacklist membership)
- Android client (direct calls to backend)
- Label-based filtering for inbox, spam, bin, etc.

## Docker Compose Service Names
- mongodb
- bloom-server
- web-server (Node backend)
- frontend (React + nginx)
Ensure env BLOOM_SERVER_HOST=bloom-server when using compose.

## Flow: Report Spam
1. Client adds "spam" label to mail.
2. Extracts URLs → POST /api/blacklist → backend → Bloom service.
3. Mail no longer appears in other user label queries.

## Bloom Filter Service
Binary launched with CLI args:
```
./bloom_filter_app <port> <capacity> <hashIds...>
```
From Dockerfile:
```
CMD ["./bloom_filter_app","4000","1000","1","2","3","4",...]
```

## Labels
System labels (non-editable): inbox, starred, sent, drafts, spam, bin, all, unread (and others if backend enforces).
User labels: stored per user, editable.

## Authentication
- Cookie-based (httpOnly) set on login.
- Android: uses 10.0.2.2 to reach host loopback.