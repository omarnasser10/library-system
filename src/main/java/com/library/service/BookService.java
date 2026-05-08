package com.library.service;

import com.library.dto.AddBookRequest;
import com.library.dto.UpdateBookRequest;
import com.library.exception.BookCannotBeDeletedException;
import com.library.exception.BookNotFoundException;
import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ============================
    // View All Books (with pagination)
    // ============================
    public Page<Book> viewAllBooks(Pageable pageable) {
        return bookRepository.findByActiveTrue(pageable);
    }

    // ============================
    // Search Books by title or author
    // ============================
    public List<Book> searchBooks(String keyword) {
        return bookRepository
                .findByActiveTrueAndTitleContainingIgnoreCaseOrActiveTrueAndAuthorContainingIgnoreCase(
                        keyword, keyword
                );
    }

    // ============================
    // Get Book by ID
    // ============================
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
    }

    // ============================
    // Add New Book
    // ============================
    public Book addBook(AddBookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());
        book.setCategory(request.getCategory());
        book.setCoverImageUrl(request.getCoverImageUrl());
        book.setPdfUrl(request.getPdfUrl());
        book.setActive(true);
        return bookRepository.save(book);
    }

    // ============================
    // Update Book
    // ============================
    public Book updateBook(Long id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (!Boolean.TRUE.equals(book.getActive())) {
            throw new BookNotFoundException("Book not found");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            book.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null && !request.getAuthor().isBlank()) {
            book.setAuthor(request.getAuthor());
        }
        if (request.getTotalCopies() != null && request.getTotalCopies() > 0) {
            int diff = request.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(request.getTotalCopies());
            book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + diff));
        }
        if (request.getCategory() != null) {
            book.setCategory(request.getCategory());
        }
        if (request.getCoverImageUrl() != null) {
            book.setCoverImageUrl(request.getCoverImageUrl());
        }
        if (request.getPdfUrl() != null) {
            book.setPdfUrl(request.getPdfUrl());
        }

        return bookRepository.save(book);
    }

    // ============================
    // Delete Book (Soft Delete)
    // ============================
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (!book.getAvailableCopies().equals(book.getTotalCopies())) {
            throw new BookCannotBeDeletedException("Cannot delete a book that has borrowed copies");
        }

        book.setActive(false);
        bookRepository.save(book);
    }
}
