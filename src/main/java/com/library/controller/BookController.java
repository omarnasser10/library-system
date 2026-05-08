package com.library.controller;

import com.library.dto.AddBookRequest;
import com.library.dto.BookResponse;
import com.library.dto.UpdateBookRequest;
import com.library.model.Book;
import com.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ======================================
    // GET /books?page=0&size=10
    // View all active books (paginated) — Any authenticated user
    // ======================================
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<BookResponse>> viewAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());

        Page<BookResponse> response = bookService
                .viewAllBooks(pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(response);
    }

    // ======================================
    // GET /books/search?keyword=java
    // Search books by title or author — Any authenticated user
    // ======================================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam String keyword) {

        List<BookResponse> response = bookService
                .searchBooks(keyword)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // ======================================
    // POST /books
    // Add a new book — Admin only
    // ======================================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody AddBookRequest request) {

        Book book = bookService.addBook(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(book));
    }

    // ======================================
    // PUT /books/{id}
    // Update book details — Admin only
    // ======================================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {

        Book book = bookService.updateBook(id, request);
        return ResponseEntity.ok(toResponse(book));
    }

    // ======================================
    // DELETE /books/{id}
    // Soft-delete a book — Admin only
    // ======================================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // ======================================
    // Helper: Book → BookResponse
    // ======================================
    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getCategory(),
                book.getCoverImageUrl()
        );
    }
}