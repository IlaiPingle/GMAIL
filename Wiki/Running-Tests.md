# Testing

## Bloom Filter (C++)
```
cd Bloom_Filter_Server
cmake -S . -B build -DBUILD_TESTS=ON
ctest --test-dir build
```

## Frontend
```
cd frontend
npm test
```

## Backend
(Add tests when created; placeholder):
```
cd backend
npm test
```

## Android
Use Android Studio:
- Unit tests: src/test
- Instrumented: src/androidTest