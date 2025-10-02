# 🛠️ User Registration & Login

This guide explains how users can **sign up and log in** to the system.

## 🚀 Authentication Flow
1. **Sign Up Page** → Enter details, and upload a profile picture.
2. **Log In Page** → Enter email & password to access the platform.
---
![Landing Page in web](./images/landing-web.png)

![Landing Page in android](./images/landing-and.png)


## 📝 **User Registration (Sign Up)**
To create an account, users need to provide:
- **username** 📧
- **Full Name** 🏷️
- **Phone Number** 📞 (exactly 10 digits)
- **Password** 🔒 (at least **8 characters**, including a **number** and special charachter)
- **Profile Picture** 🖼️ (image file - png/jpeg)

## You'll get a success message if the sign-up is successful, and an error message if it fails with the reason. Note that the username must be unique, and the password must meet the requirements. After successful registration, you'll be redirected to the login page.

![Sign Up Page in web](./images/signup-web.png)
![Sign Up Page in android](./images/signup-and.png)

---

## 📝 **User Login**
To access the platform, users need to:
- **Enter Email** 📧
- **Enter Password** 🔒

![Login Page](./images/sig-in-web.png)

## You'll get a success message if the login is successful, and an error message if the login fails.

---

## In a case of an already signed-in user, the system will automatically redirect to the home screen of the user. In that page you'll see in the up-right corner the user's name and profile picture, with a button of "Log Out" - that will log out the user from the system back to the landing page. 

![Home Screen, sign-out in the top-right corner](./images/home-options-weeb.png)

- For un-logged users that try to access the Netflix pages (such as home page) - they will be redirected to the landing page with an alert message that they need to log in to access the page.
