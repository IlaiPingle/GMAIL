#!/bin/bash

print_section() {
  echo -e "\n\033[1;34m== $1 ==\033[0m"
}

run_curl() {
  echo -e "\033[1;33m$1\033[0m"
  eval "$1"
  echo ""
}

print_section "🧹 Registering users"
run_curl "curl -i -X POST http://localhost:8080/api/users -H 'Content-Type: application/json' -d '{
  "id": 1,
  "username": "alice",
  "password": "1234",
  "first_name": "Alice",
  "sur_name": "Wonder",
  "picture": "https://example.com/alice.jpg"
}'"

run_curl "curl -i -X POST http://localhost:8080/api/users -H 'Content-Type: application/json' -d '{
  "id": 2,
  "username": "bob",
  "password": "abcd",
  "first_name": "Bob",
  "sur_name": "Builder",
  "picture": "https://example.com/bob.jpg"
}'"

print_section "🔐 Logging in users"
run_curl "curl -i -X POST http://localhost:8080/api/tokens -H 'Content-Type: application/json' -d '{"username": "alice", "password": "1234"}'"
run_curl "curl -i -X POST http://localhost:8080/api/tokens -H 'Content-Type: application/json' -d '{"username": "bob", "password": "abcd"}'"

print_section "📬 Sending emails"
run_curl "curl -i -X POST http://localhost:8080/api/mails -H 'Content-Type: application/json' -H 'user-id: 1' -d '{
  "sender": "alice",
  "receiver": "bob",
  "subject": "Meeting",
  "body": "Can we meet tomorrow at 10?",
  "dateCreated": "2025-07-02T12:00:00.000Z"
}'"

run_curl "curl -i -X POST http://localhost:8080/api/mails -H 'Content-Type: application/json' -H 'user-id: 2' -d '{
  "sender": "bob",
  "receiver": "alice",
  "subject": "Reply",
  "body": "Sure, see you at 10.",
  "dateCreated": "2025-07-02T13:00:00.000Z"
}'"

print_section "🚫 Adding blacklisted URL"
run_curl "curl -i -X POST http://localhost:8080/api/blacklist -H 'Content-Type: application/json' -H 'user-id: 1' -d '{"url": "http://malicious.com"}'"

print_section "🚫 Trying to send blacklisted URL"
run_curl "curl -i -X POST http://localhost:8080/api/mails -H 'Content-Type: application/json' -H 'user-id: 1' -d '{
  "sender": "alice",
  "receiver": "bob",
  "subject": "Dangerous Link",
  "body": "Check this: http://malicious.com",
  "dateCreated": "2025-07-02T15:00:00.000Z"
}'"

print_section "📥 Fetching inboxes"
run_curl "curl -i http://localhost:8080/api/mails -H 'user-id: 1'"
run_curl "curl -i http://localhost:8080/api/mails -H 'user-id: 2'"

print_section "🔍 Searching emails"
run_curl "curl -i http://localhost:8080/api/mails/search?q=meeting -H 'user-id: 1'"

print_section "🏷️ Creating and managing labels"
run_curl "curl -i -X POST http://localhost:8080/api/labels -H 'Content-Type: application/json' -H 'user-id: 1' -d '{"labelName": "Work"}'"
run_curl "curl -i -X POST http://localhost:8080/api/labels -H 'Content-Type: application/json' -H 'user-id: 2' -d '{"labelName": "Friends"}'"
run_curl "curl -i http://localhost:8080/api/labels -H 'user-id: 1'"
run_curl "curl -i http://localhost:8080/api/labels -H 'user-id: 2'"

print_section "📝 PATCH label name"
run_curl "curl -i -X PATCH http://localhost:8080/api/labels/Work -H 'Content-Type: application/json' -H 'user-id: 1' -d '{"newName": "Work-Updated"}'"

print_section "🗑️ DELETE label"
run_curl "curl -i -X DELETE http://localhost:8080/api/labels/Work-Updated -H 'user-id: 1'"

print_section "📄 Getting user details"
run_curl "curl -i http://localhost:8080/api/users/1 -H 'user-id: 1'"
run_curl "curl -i http://localhost:8080/api/users/2 -H 'user-id: 2'"

print_section "🗑️ DELETE blacklisted URL"
run_curl "curl -i -X DELETE "http://localhost:8080/api/blacklist/http%3A%2F%2Fmalicious.com" -H 'user-id: 1'"
