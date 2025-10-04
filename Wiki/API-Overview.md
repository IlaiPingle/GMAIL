# API Overview

Base: http://localhost:8080 (web)  
Android emulator: http://10.0.2.2:8080

## Auth
POST /users/register  
POST /users/login  
GET /users/me

## Emails
GET /emails
GET /emails/:id
POST /emails
PATCH /emails/:id
DELETE /emails/:id (hard delete)
(label mutation via label routes)

## Labels
GET /labels
POST /labels       { labelName }
PATCH /labels/:id  { newName }
DELETE /labels/:id

## Mail <-> Label
POST /labels/mails/:mailId    { labelName }
DELETE /labels/mails/:mailId  { labelName }

## Blacklist
POST /blacklist  { url }
(Backend forwards to Bloom service.)

## Notes
- All protected routes require auth cookie.
- Label rename uses key "newName".
- Avoid combining $addToSet + $pull on same array path (handled via pipeline now).