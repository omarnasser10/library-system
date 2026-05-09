# 🧪 LibSystem Testing & Quality Assurance Report

This document serves as the official testing documentation for the LibSystem project, detailing the test cases, bug reporting process, and QA workflow.

---

## 1. Test Cases Suite
Each task/ticket in Jira was associated with specific test cases to ensure functional correctness.

### 1.1 Authentication Module
| Test Case ID | Feature | Description | Expected Result | Status |
| :--- | :--- | :--- | :--- | :--- |
| TC-AUTH-01 | Login | Login with valid credentials | Redirect to Dashboard | ✅ Pass |
| TC-AUTH-02 | Login | Login with wrong password | Show "Invalid credentials" error | ✅ Pass |
| TC-AUTH-03 | Register | Register with existing email | Show "Email already exists" error | ✅ Pass |

### 1.2 Book Management (Admin)
| Test Case ID | Feature | Description | Expected Result | Status |
| :--- | :--- | :--- | :--- | :--- |
| TC-BOOK-01 | Add Book | Add book with missing title | Validation error on title field | ✅ Pass |
| TC-BOOK-02 | PDF Upload | Upload file > 10MB | Show "File too large" error | ✅ Pass |
| TC-BOOK-03 | Delete Book | Remove book with active borrows | Block deletion / Warn admin | ✅ Pass |

### 1.3 Borrowing Module (Member)
| Test Case ID | Feature | Description | Expected Result | Status |
| :--- | :--- | :--- | :--- | :--- |
| TC-BORR-01 | Borrow | Borrow book with 0 copies | "Borrow" button disabled | ✅ Pass |
| TC-BORR-02 | Reader | Open PDF without active borrow | Show "403 Forbidden" / Access Denied | ✅ Pass |

---

## 2. Bug Reports Log
Below is a history of critical bugs identified and resolved during the development lifecycle.

### BUG-042: Mobile Responsiveness in PDF Reader
**Severity:** High | **Status:** Fixed
- **Issue:** PDF content was cut off on mobile screens.
- **Root Cause:** Fixed width in CSS iframe container.
- **Fix:** Changed to responsive `vw/vh` units.

### BUG-043: Negative Book Copies (Validation)
**Severity:** Medium | **Status:** Fixed
- **Issue:** Admin was able to save a book with `-5` total copies.
- **Root Cause:** Missing `@Min(1)` annotation on the `AddBookRequest` DTO.
- **Fix:** Added Hibernate validation constraints and frontend range checks.

### BUG-044: Double Borrowing Logic
**Severity:** High | **Status:** Fixed
- **Issue:** A user could borrow the same book multiple times simultaneously.
- **Root Cause:** Missing database-level check for active borrows by the same user for the same book.
- **Fix:** Added `existsByUserIdAndBookIdAndStatus` check in `BorrowService`.

### BUG-045: SQL Search Crash (500 Error)
**Severity:** Medium | **Status:** Fixed
- **Issue:** Using special characters like `%` or `'` in the search bar caused a server crash.
- **Root Cause:** Improper handling of SQL escape characters in the repository query.
- **Fix:** Switched to parameterized JPA queries to prevent injection and handle special characters.

### BUG-046: JWT Expiry Redirection
**Severity:** Low | **Status:** Fixed
- **Issue:** When the token expired, the user stayed on the dashboard until they refreshed.
- **Root Cause:** Missing 401 error interceptor on the frontend.
- **Fix:** Implemented an Axios interceptor to auto-redirect to `/login` upon any 401 response.

---

## 3. QA Workflow (The Jira Pipeline)
We followed a strict **Bug Life Cycle** to maintain software quality:

1.  **To Do / Backlog**: New features or bugs are identified.
2.  **In Progress**: Developer is working on the code.
3.  **Review / PR**: Peer review of the code.
4.  **Testing (QA)**: **[My Primary Role]** I pull the latest code and execute the Test Cases.
    - If the feature works: Move to **Done**.
    - If a bug is found: Move to **Re-opened** and attach a Bug Report.
5.  **Done**: Feature is fully verified and deployed.

---

## 4. Testing Techniques Used
- **Black Box Testing**: Testing the UI and APIs without looking at the internal code to ensure user requirements are met.
- **Boundary Value Analysis**: Testing limits like "0 copies available" or "File size limits".
- **Positive & Negative Testing**: Testing correct inputs and deliberately testing wrong inputs to ensure proper error handling.
