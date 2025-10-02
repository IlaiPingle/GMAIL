# 📧 Gmail App Setup Guide

A full-stack Gmail-like application with **Node.js backend**, **React frontend**, **C++ Bloom Filter server**, **MongoDB**, and **Android client**.

---

## 📑 Table of Contents
1. [Prerequisites](#prerequisites)
2. [Cloning the Repository](#cloning-the-repository)
3. [Navigating to the Project Directory](#navigating-to-the-project-directory)
4. [Environment Setup](#environment-setup)
5. [Environment Variables Explained](#environment-variables-explained)
6. [Running the Project - Servers and React](#running-the-project---servers-and-react)
7. [Running the Android Client](#running-the-android-client)

---

## 🔧 Prerequisites
Before you begin, ensure the following tools are installed on your system:

- **Docker** and **Docker Compose** → [Install here](https://www.docker.com/)  
- **Android Studio** → [Install here](https://developer.android.com/studio)  
- **MongoDB** (if not using dockerized version) → [Install here](https://www.mongodb.com/)

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

To run the project smoothly, configure environment variables.

Create a `.env` file inside **`EX1/backend/`** with the following values:

```env
MONGODB_URI=mongodb://mongodb:27017/maildb
PORT=8080
JWT_SECRET=change-me
BLOOM_SERVER_HOST=bloom-server
BLOOM_SERVER_PORT=4000
UPLOAD_DIR=/app/uploads
```

🔹 **Android client uses**:

```
Base URL: http://10.0.2.2:8080
```

🔹 **Frontend (optional .env)**:

```env
REACT_APP_API_BASE=http://localhost:8080
```

---

## 📌 Environment Variables Explained

| Variable             | Purpose                                       |
| -------------------- | --------------------------------------------- |
| `MONGODB_URI`        | Connection string to the MongoDB service      |
| `PORT`               | Port for the backend Node.js server           |
| `JWT_SECRET`         | Secret key for signing authentication tokens  |
| `BLOOM_SERVER_HOST`  | Host for the C++ Bloom filter server          |
| `BLOOM_SERVER_PORT`  | Port for the Bloom filter server              |
| `UPLOAD_DIR`         | Directory for uploaded files inside container |
| `REACT_APP_API_BASE` | API base URL for React app (frontend only)    |

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

   * Backend API → [http://localhost:8080](http://localhost:8080)
   * Frontend UI → [http://localhost:3000](http://localhost:3000)
   * MongoDB → `mongodb:27017` (or `localhost:27017` from host)

2. **Stop the Server**

   ```bash
   docker-compose down
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

---

✅ That’s it! You now have the **Gmail App** running locally with backend, frontend, and mobile client.
