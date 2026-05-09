# 📚 LibSystem: Premium E-Library Management System

LibSystem is a professional, full-stack E-Library solution designed for digital license management and secure manuscript streaming. It features a stunning glassmorphic UI, robust JWT authentication, and a scalable Spring Boot backend.

---

## 🚀 Key Features

### 👥 For Members
- **Interactive Catalog**: Browse a rich collection of books with real-time availability tracking.
- **Digital License Borrowing**: Instant borrowing of digital copies (manuscripts).
- **Secure Reader**: Integrated PDF viewer with authenticated streaming to prevent unauthorized downloads.
- **Personal Library**: Track active borrows, due dates, and reading progress.

### 🛡️ For Administrators
- **Management Console**: Full CRUD operations for the book inventory.
- **License Oversight**: Monitor global borrowing activity and manual license revocation.
- **Member Management**: Track registered users and system activity.
- **Analytics Dashboard**: Real-time stats on library health and popularity.
- **Manuscript Upload**: Integrated PDF file upload and storage system.

---

## 🛠️ Technical Stack

### **Backend (Spring Boot)**
- **Language**: Java 21
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security + JWT (Stateless)
- **Database**: MySQL 8.0
- **Persistence**: Spring Data JPA (Hibernate)
- **API Documentation**: Swagger/OpenAPI 3

### **Frontend (Vue.js)**
- **Framework**: Vue 3 (Composition API)
- **State Management**: Pinia
- **Routing**: Vue Router 4
- **Design**: Vanilla CSS (Premium Glassmorphism System)
- **Icons**: FontAwesome 6

---

## ⚙️ Installation & Setup

### **1. Prerequisites**
- JDK 21+
- MySQL Server
- Node.js (v18+)

### **2. Backend Configuration**
1. Navigate to `library-system_backend`.
2. Update `src/main/resources/application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD
   ```
3. Run the application:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
   *The system will automatically seed default data (Admin: `admin@library.com` / `admin123`).*

### **3. Frontend Configuration**
1. Navigate to `library_system`.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```

---

## 📂 Database Schema & Seeding
The system uses an automated Seeder (`DatabaseSeeder.java`) that populates the following:
- **Users**: 1 Admin, 4 Sample Members.
- **Books**: 10 High-quality technical and self-help titles.
- **Transactions**: Sample borrow history for immediate testing.

---

## 📑 API Overview

| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| POST | `/auth/login` | User login (returns JWT) | Public |
| POST | `/auth/register` | Member registration | Public |
| GET | `/books` | List all books (paginated) | Token |
| POST | `/books` | Add new book | Admin |
| POST | `/books/{id}/upload-pdf` | Upload PDF file | Admin |
| GET | `/books/{id}/read` | Stream PDF content | Member/Admin |
| POST | `/borrows/book/{id}` | Borrow a book | Member |

---

## 🎨 Design Philosophy: Glassmorphism
LibSystem utilizes a modern **Glassmorphism** design language characterized by:
- **Translucency**: Frosted glass effects on panels using `backdrop-filter: blur()`.
- **Vibrant Gradients**: Deep indigo and slate background with subtle radial glows.
- **Micro-Animations**: Smooth transitions and hover effects for a premium feel.

---

## 📝 License
This project is developed as a professional portfolio piece for E-Library Management.
