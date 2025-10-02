# 📧 Gmail App Setup Guide

A full-stack Gmail-like application with **Node.js backend**, **React frontend**, **C++ Bloom Filter server**, **MongoDB**, and **Android client**.

---

## 📑 Table of Contents
1. [Prerequisites](#prerequisites)  
2. [Cloning the Repository](#cloning-the-repository)  
3. [Navigating to the Project Directory](#navigating-to-the-project-directory)  
4. [Environment Setup](#environment-setup)  
   - [Backend `.env`](#backend-env)  
   - [Frontend `.env`](#frontend-env)  
   - [Bloom Filter Server `.env`](#bloom-filter-server-env)  
   - [Android `.env`](#android-env)  
5. [Environment Variables Explained](#environment-variables-explained)  
6. [Running the Project - Servers and React](#running-the-project---servers-and-react)  
7. [Running the Android Client](#running-the-android-client)

---

## 🔧 Prerequisites
Before you begin, ensure the following tools are installed on your system:

- **Docker** and **Docker Compose** → https://www.docker.com/  
- **Android Studio** → https://developer.android.com/studio  
- **MongoDB** (if not using dockerized version) → https://www.mongodb.com/

---

## 📥 Cloning the Repository
1. Open **Terminal** or **Command Prompt**.  
2. Clone the repository:
   ```bash
   git clone https://github.com/matanshaul7/EX1.git
   ```

---

## 📂 Navigating to the Project Directory
After cloning, go to the project folder:
```bash
cd Gmail
```
This directory contains all necessary **Dockerfiles** and the **docker-compose** configuration.

---

## ⚙️ Environment Setup
Below are the required `.env` files for each component. **Do not commit real `.env` files**. Commit only `*.env.example` versions.

### Backend `.env`
**Path:** `EX1/backend/.env`
```env
# Server
PORT=8080
JWT_SECRET=change-me

# Database (use the Docker service name, not localhost)
MONGODB_URI=mongodb://mongodb:27017/maildb

# Bloom Filter server (Docker-internal host & port)
BLOOM_SERVER_HOST=bloom-server
BLOOM_SERVER_PORT=4000

# Uploads directory inside the container (mapped via docker-compose volume)
UPLOAD_DIR=/app/uploads

# CORS - comma-separated list of allowed browser origins (for React UI)
# For local dev:
CORS_ORIGINS=http://localhost:3000,http://127.0.0.1:3000
```

---

### Frontend `.env`
**Path:** `EX1/frontend/.env` (read at **build-time** by React)
```env
# React build-time variables must start with REACT_APP_
REACT_APP_API_BASE=http://localhost:8080
REACT_APP_ENV=local
```

> React (CRA/Vite) **bakes** these values into the built static files. If you change them, rebuild the frontend image.

---

### Bloom Filter Server `.env`
**Path:** `EX1/Bloom_Filter_Server/.env` (optional, if your C++ server supports env)
```env
BLOOM_PORT=4000
# BLOOM_DATA_PATH=/data/blacklist.txt   # example if relevant
```
> The backend connects to this service at `bloom-server:4000` inside Docker.

---

### Android `.env`
**Path:** `EX1/android/.env` (loaded by Gradle into `BuildConfig`)
```env
# For Android emulator to reach your host's backend
API_BASE_URL=http://10.0.2.2:8080
ENV=local
```
In `app/build.gradle.kts`, load the file and define:
```kotlin
import java.util.Properties

fun loadDotEnv(path: String): Properties {
    val props = Properties()
    val file = rootProject.file(path)
    if (file.exists()) file.inputStream().use { props.load(it) }
    return props
}

val env = loadDotEnv("android/.env")

android {
    // ...
    defaultConfig {
        // ...
        buildConfigField("String", "API_BASE_URL", ""${env.getProperty("API_BASE_URL", "http://10.0.2.2:8080")}"")
        buildConfigField("String", "ENV", ""${env.getProperty("ENV", "local")}"")
    }
}
```
---

## 📌 Environment Variables Explained

### Backend (`backend/.env`)
| Variable              | Purpose                                                                 |
|-----------------------|-------------------------------------------------------------------------|
| `PORT`                | Port for the Node.js backend inside the container (exposed to host).    |
| `JWT_SECRET`          | Secret key for signing authentication tokens.                           |
| `MONGODB_URI`         | Connection string to MongoDB (use `mongodb://mongodb:27017/...`).       |
| `BLOOM_SERVER_HOST`   | Docker-internal hostname of the Bloom server (usually `bloom-server`).  |
| `BLOOM_SERVER_PORT`   | Port of the Bloom server (usually `4000`).                              |
| `UPLOAD_DIR`          | Directory for uploaded files inside the backend container.              |
| `CORS_ORIGINS`        | Comma-separated list of allowed **browser** origins (React UI).         |

> Note: Native Android apps (Retrofit/OkHttp) are **not** subject to browser CORS.

### Frontend (`frontend/.env`)
| Variable               | Purpose                                                     |
|------------------------|-------------------------------------------------------------|
| `REACT_APP_API_BASE`   | Base URL for API calls from the React app (build-time).    |
| `REACT_APP_ENV`        | Arbitrary label for environment (e.g., `local`, `prod`).   |

> These are compiled into the static bundle. Rebuild the image after changes.

### Bloom Filter Server (`Bloom_Filter_Server/.env`)
| Variable          | Purpose                              |
|-------------------|--------------------------------------|
| `BLOOM_PORT`      | Port the C++ Bloom server listens on |
| `BLOOM_DATA_PATH` | Optional path to data/blacklist file |

### Android (`android/.env`)
| Variable        | Purpose                                                          |
|-----------------|------------------------------------------------------------------|
| `API_BASE_URL`  | Backend API base for the Android client (e.g., `http://10.0.2.2:8080`). |
| `ENV`           | Arbitrary label (e.g., `local`, `prod`).                         |

---

## 🚀 Running the Project - Servers and React

1. **Build & Run**
   ```bash
   docker compose up --build
   ```

   **Available Services (via docker-compose):**

   | Service      | Purpose          | Host Port |
   | ------------ | ---------------- | --------- |
   | web-server   | Node.js backend  | 8080      |
   | frontend     | React (nginx)    | 3000      |
   | bloom-server | C++ Bloom filter | 4000      |
   | mongodb      | MongoDB database | 27017     |

   * Backend API → <http://localhost:8080>  
   * Frontend UI → <http://localhost:3000>  
   * MongoDB → `mongodb:27017` (or `localhost:27017` from host if port mapped)

2. **Stop the Server**
   ```bash
   docker compose down
   ```
   or use `CTRL + C` in the terminal.

---

## 📱 Running the Android Client

1. Open **Android Studio**.
2. Load the **Android project** inside the repository.
3. Run on:
   * **Physical Device** → Enable developer mode & USB debugging.
   * **Emulator** → Set up via **AVD Manager**.
4. Press ▶ (Play) in Android Studio to build & run the app.

> For the emulator, the backend is typically reachable at `http://10.0.2.2:8080` — configured in `android/.env`.

---

✅ That’s it! You now have the **Gmail App** running locally with backend, frontend, and mobile client — all with clearly separated, environment-specific configuration via `.env` files.
