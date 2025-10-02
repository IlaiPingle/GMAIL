# 🛠️ User Registration & Login

This guide explains how users can **sign up and log in** to the system.

---

## 🚀 Authentication Flow
1. **Sign Up Page** → User enters details and uploads a profile picture.  
2. **Log In Page** → User enters email & password to access the platform.  
3. **Home Screen** → Authenticated users are redirected automatically to their dashboard.  

---

### 🌐 Landing Pages
![Landing Page - Web](./images/landing-web.png)  
![Landing Page - Android](./images/landing-and.png)  

---

## 📝 User Registration (Sign Up)
To create a new account, users must provide:

- **Username (unique)** ✉️  
- **Full Name** 🏷️  
- **Phone Number** 📞 (exactly 10 digits)  
- **Password** 🔒 (minimum **8 characters**, must include a **number** and a **special character**)  
- **Profile Picture** 🖼️ (image file: `.png` / `.jpeg`)  

✅ **On success:** The system shows a confirmation message and redirects to the **Login Page**.  
❌ **On error:** The system displays the reason (e.g., *username already exists*, *invalid password format*).  

---

### 🖼️ Sign Up Screens
![Sign Up - Web](./images/signup-web.png)  
![Sign Up - Android](./images/signup-and.png)  

---

## 🔑 User Login
To access the platform, users need to:

- **Enter Username/Email** ✉️  
- **Enter Password** 🔒  

✅ **On success:** Users are redirected to the **Home Screen**.  
❌ **On error:** The system displays an error message (e.g., *wrong credentials*).  

---

### 🖼️ Login Screen
![Login Page - Web](./images/sig-in-web.png)  

---

## 🏠 Home Screen & Session Management
- Once logged in, users are redirected to the **Home Screen**.  
- At the **top-right corner**, the user’s **name** and **profile picture** are displayed.  
- A **Log Out** button is available → clicking it logs the user out and redirects to the **Landing Page**.  

![Home Screen - Web (with logout option)](./images/home-options-weeb.png)  

---

## 🔒 Unauthorized Access
- If a **non-logged-in user** tries to access restricted pages (e.g., **Home Page**), the system will:  
  1. Redirect them to the **Landing Page**.  
  2. Display an **alert message**: *“You must log in to access this page.”*  

---

✨ With this flow, authentication is **secure, user-friendly, and consistent** across both **Web** and **Android** platforms.
