# Banking System

A full-featured online banking web app built using Spring Boot and Java EE. Built as part of a course project for CSD 4464 at Lambton College.

---

## 🚀 Features

- 📝 User Registration & Login (with session-based authentication)
- 🔐 OTP Email Verification using Gmail SMTP
- 🏦 Multiple Account Types (Savings, Checking, Business)
- 💵 Deposit & Withdrawal (with balance validation)
- 🔄 Internal Transfers Between User Accounts
- 📄 Transaction History with Pagination & Filtering
- 🧾 Utility Bill Payments (Electricity, Water, etc.)
- ✉️ Email Notifications for Transactions (Optional)
- 🔒 Two-Factor Authentication (2FA) for High-Value Transfers
- 🗃️ Organized MVC Folder Structure (Controller, Service, Model, etc.)

---

## 🧰 Tech Stack

| Layer     | Technology           |
|-----------|----------------------|
| Backend   | Java EE (Spring Boot)|
| Frontend  | Thymeleaf (HTML/CSS) |
| Database  | MySQL                |
| Security  | Spring Security, Bcrypt |
| Tools     | Maven, Git, GitHub, VS Code |

---

## 📁 Folder Structure
banking/
### ├── controller/ # Handles HTTP requests
### ├── model/ # Contains entity classes
### ├── repository/ # Interfaces for database access
### ├── service/ # Business logic layer
### ├── config/ # Spring Security and application config
### ├── templates/ # Thymeleaf HTML templates
### ├── static/ # CSS, JS, images
### └── application.properties # Configuration for database, server, etc.