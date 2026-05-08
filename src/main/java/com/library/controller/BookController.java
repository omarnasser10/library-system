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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.library.service.FileStorageService;
import com.library.service.BorrowService;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final FileStorageService fileStorageService;
    private final BorrowService borrowService;

    public BookController(BookService bookService, 
                          FileStorageService fileStorageService,
                          BorrowService borrowService) {
        this.bookService = bookService;
        this.fileStorageService = fileStorageService;
        this.borrowService = borrowService;
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
    // POST /books/{id}/upload-pdf
    // Upload PDF for a book — Admin only
    // ======================================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/upload-pdf")
    public ResponseEntity<BookResponse> uploadBookPdf(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Book book = bookService.getBookById(id);
        
        String fileName = fileStorageService.storeFile(file);
                
        UpdateBookRequest updateRequest = new UpdateBookRequest();
        updateRequest.setPdfUrl(fileName);
        
        Book updatedBook = bookService.updateBook(id, updateRequest);
        return ResponseEntity.ok(toResponse(updatedBook));
    }

    // ======================================
    // GET /books/{id}/read
    // Read the book's PDF — User must have borrowed it
    // ======================================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/read")
    public ResponseEntity<Resource> readBook(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        
        if (book.getPdfUrl() == null || book.getPdfUrl().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Admin can read any book, User must have borrowed it
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                
        if (!isAdmin && !borrowService.hasActiveBorrow(userId, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Resource resource = fileStorageService.loadFileAsResource(book.getPdfUrl());
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
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
                book.getCoverImageUrl(),
                book.getPdfUrl());
    }
}