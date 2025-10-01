# Bloom Filter Server

## Source
./Bloom_Filter_Server

## Build Locally
```
cd Bloom_Filter_Server
cmake -S . -B build
cmake --build build -j
./build/bloom_filter_app 4000 1000 1 2 3 4 5
```

## Docker (used in compose)
Dockerfile:
- Based on ubuntu:22.04
- Installs build-essential + cmake
- Builds target
- Exposes 4000

## Runtime Args
In Dockerfile CMD:
```
["./bloom_filter_app","4000","1000","1","2","3","..."]
```
Meaning:
- Port: 4000
- Capacity: 1000
- Hash strategy IDs: sequence

## Responsibilities
- Add URL
- Check URL membership
- Remove URL
(Accessed indirectly via backend service: src/services/blacklistClient.js)