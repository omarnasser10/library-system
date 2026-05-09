# Software Requirements Specification (SRS) - LibSystem

## 1. Introduction
LibSystem is an automated Library Management System designed to handle digital book (PDF) borrowing and management. It ensures that digital copies are distributed based on available licenses and provides a secure environment for reading.

## 2. User Roles
### 2.1 Member (User)
- Register and Login.
- Browse the book catalog.
- Search for books by title, author, or category.
- Borrow books (if copies are available).
- View active borrows and due dates.
- Read PDF content via a secure integrated viewer.
- Return borrowed books.

### 2.2 Administrator
- All Member privileges.
- Add, update, and delete books from the inventory.
- Upload PDF files for books.
- Monitor all borrowing transactions across the system.
- Forcefully return/recall books from users.
- View and manage all registered members.
- Access system-wide analytics.

## 3. Functional Requirements
- **FR1 (Authentication)**: The system shall use JWT-based stateless authentication.
- **FR2 (License Management)**: The system shall decrement `availableCopies` when a book is borrowed and increment it when returned.
- **FR3 (Concurrency)**: The system shall prevent borrowing if `availableCopies` is 0.
- **FR4 (Auto-Return)**: Books shall have a default borrowing period of 14 days (logic handled by backend).
- **FR5 (Secure Streaming)**: PDF files shall not be directly downloadable; they must be streamed via authenticated endpoints.

## 4. Non-Functional Requirements
- **Security**: Sensitive data like passwords must be BCrypt encoded.
- **Performance**: API responses should be under 200ms for standard queries.
- **Scalability**: The system should support hundreds of concurrent users.
- **Usability**: The UI must be responsive and follow modern design principles (Glassmorphism).

## 5. System Architecture
- **Frontend**: Single Page Application (SPA) using Vue.js.
- **Backend**: RESTful API using Spring Boot.
- **Database**: Relational database (MySQL).
- **Storage**: Local filesystem storage for PDF assets (configurable for Cloud Storage).

## 6. Data Models
### 6.1 User
- ID, Name, Email, Password, Role (ADMIN/MEMBER).

### 6.2 Book
- ID, Title, Author, TotalCopies, AvailableCopies, Category, CoverImageUrl, PDFUrl, IsActive.

### 6.3 Borrow
- ID, User, Book, BorrowDate, DueDate, ReturnDate, Status (BORROWED/RETURNED).
