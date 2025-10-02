# **Gmail App** 📬📨✨

Welcome to the **Gmail App Project**, a full-stack application that simulates Gmail with authentication, labels, inbox management, and a Bloom Filter–based blacklist system.

---

## **Table of Contents**

1. [Project Overview](#project-overview)  
2. [Technologies Used](#technologies-used)  
3. [Setup and Installation](#setup-and-installation)  
4. [Development Process](#development-process)  

---

## **Project Overview**

This project is a **Gmail-like mail system** designed for both **Web** and **Android** platforms.  
The system allows users to:

- 📝 Register and log in (with JWT-based authentication).  
- 📥 Manage inbox, sent mails, drafts, and labels.  
- 📤 Compose, send, and delete emails.  
- 🏷️ Create and apply custom labels.  
- 🚫 Use a **C++ Bloom Filter server** to check against blacklisted URLs.  
- ☁️ Store all user and mail data in MongoDB.  

The architecture is **client–server**, where the Node.js backend communicates with MongoDB and the Bloom Filter server. The React frontend and Android app consume the backend APIs.  

All services (backend, frontend, bloom filter, database) are **containerized using Docker** and orchestrated with Docker Compose.

---

## **Technologies Used**

- **Docker & Docker Compose** → Containerizing backend, frontend, and services.  
- **Node.js + Express** → Backend web server with REST API.  
- **React (with Nginx)** → Frontend web client.  
- **C++** → Bloom Filter server for blacklist checks.  
- **MongoDB** → Database for users, emails, labels.  
- **JWT (JSON Web Tokens)** → Secure authentication.  
- **Android Studio** → Native Android client.  
- **Room DB (Android)** → Local caching of user mails.  

---

## **Setup and Installation**

For full details, see the [Wiki Setup Guide](wiki/01_setup.md).

### **Quick Setup Steps:**

1. **Clone the Repository**
   ```
   git clone https://github.com/<your-username>/Gmail.git
   cd Gmail
    ```

2. **Configure Environment Variables**
   Follow the instructions in the [Wiki](wiki/01_setup.md#environment-setup) to set up the `.env` file.

4. **Run with Docker**

   ```bash
   docker compose up --build
   ```

5. **Run the Android Client**

   * Open in Android Studio.
   * Configure emulator or connect device.
   * Press ▶ Run.

---

## **Development Process**

1. **Bloom Filter Server (C++)**
   Developed a multithreaded server handling URL blacklist checks.

2. **Backend (Node.js)**

   * REST API for user authentication, inbox, labels, and mails.
   * Integration with MongoDB and Bloom Filter server.
   * JWT authentication with cookies.

3. **Frontend (React)**

   * Gmail-style interface with sidebar, labels, inbox, and compose modal.
   * Dark/light mode toggle and responsive design.

4. **Android App**

   * Native client with login, inbox, labels, compose, and swipe-to-delete.
   * Local Room DB for offline mode.

---

Enjoy mailing! 📬✨📱💻
