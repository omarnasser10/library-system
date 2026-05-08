package com.library.controller;

import com.library.dto.BorrowResponse;
import com.library.model.Borrow;
import com.library.service.BorrowService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrows")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // ======================================
    // POST /borrows/{bookId}
    // Borrow a book (authenticated user)
    // ======================================
    @PostMapping("/{bookId}")
    public ResponseEntity<BorrowResponse> borrowBook(@PathVariable Long bookId) {

        Long userId = getCurrentUserId();
        Borrow borrow = borrowService.borrowBook(userId, bookId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(borrow));
    }

    // ======================================
    // POST /borrows/{bookId}/return
    // Return a book (user returns own book, admin can return any)
    // ======================================
    @PostMapping("/{bookId}/return")
    public ResponseEntity<BorrowResponse> returnBook(@PathVariable Long bookId) {

        Long userId = getCurrentUserId();
        Borrow borrow = borrowService.returnBook(userId, bookId);

        return ResponseEntity.ok(toResponse(borrow));
    }

    // ======================================
    // POST /borrows/{borrowId}/admin-return
    // Return a book by borrow ID — Admin only
    // ======================================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{borrowId}/admin-return")
    public ResponseEntity<BorrowResponse> adminReturnBook(@PathVariable Long borrowId) {
        Borrow borrow = borrowService.returnBorrowById(borrowId);
        return ResponseEntity.ok(toResponse(borrow));
    }

    // ======================================
    // GET /borrows/me
    // View my borrow history (authenticated user)
    // ======================================
    @GetMapping("/me")
    public ResponseEntity<List<BorrowResponse>> getMyBorrows() {

        Long userId = getCurrentUserId();
        List<BorrowResponse> response = borrowService
                .getMyBorrows(userId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // ======================================
    // GET /borrows
    // View ALL borrows — Admin only (paginated)
    // ======================================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<BorrowResponse>> getAllBorrows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("borrowDate").descending());

        Page<BorrowResponse> response = borrowService
                .getAllBorrows(pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(response);
    }

    // ======================================
    // GET /borrows/users/{userId}
    // View borrows of a specific user — Admin only (paginated)
    // ======================================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<BorrowResponse>> getBorrowsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("borrowDate").descending());

        Page<BorrowResponse> response = borrowService
                .getBorrowsByUserId(userId, pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(response);
    }

    // ======================================
    // Helper: Get current user's ID from SecurityContext
    // ======================================
    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // ======================================
    // Helper: Borrow → BorrowResponse
    // ======================================
    private BorrowResponse toResponse(Borrow borrow) {
        return new BorrowResponse(
                borrow.getId(),
                borrow.getBook().getId(),
                borrow.getBook().getTitle(),
                borrow.getUser().getId(),
                borrow.getUser().getName(),
                borrow.getUser().getEmail(),
                borrow.getBorrowDate(),
                borrow.getDueDate(),
                borrow.getReturnDate(),
                borrow.getStatus()
        );
    }
}
