package com.library.service;

import com.library.exception.*;
import com.library.model.Book;
import com.library.model.Borrow;
import com.library.model.BorrowStatus;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import com.library.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public BorrowService(BorrowRepository borrowRepository,
                         UserRepository userRepository,
                         BookRepository bookRepository) {
        this.borrowRepository = borrowRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    // ============================
    // Borrow a Book
    // ============================
    @Transactional
    public Borrow borrowBook(Long userId, Long bookId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (!Boolean.TRUE.equals(book.getActive())) {
            throw new BookNotFoundException("Book not found");
        }

        if (borrowRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, BorrowStatus.BORROWED)) {
            throw new BookAlreadyBorrowedException("You already have this book borrowed");
        }

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new NoAvailableCopiesException("No available copies for this book");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.now());
        borrow.setReturnDate(null);
        borrow.setStatus(BorrowStatus.BORROWED);

        return borrowRepository.save(borrow);
    }

    // ============================
    // Return a Book
    // ============================
    @Transactional
    public Borrow returnBook(Long userId, Long bookId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (!Boolean.TRUE.equals(book.getActive())) {
            throw new BookNotFoundException("Book not found");
        }

        Borrow borrow;

        if (isAdmin()) {
            borrow = borrowRepository
                    .findFirstByBookIdAndStatusOrderByBorrowDateAsc(bookId, BorrowStatus.BORROWED)
                    .orElseThrow(() -> new BookAlreadyReturnedException("This book is not currently borrowed"));
        } else {
            borrow = borrowRepository
                    .findByUserIdAndBookIdAndStatus(userId, bookId, BorrowStatus.BORROWED)
                    .orElseThrow(() -> new BookAlreadyReturnedException("You have not borrowed this book"));

            if (!borrow.getUser().getId().equals(user.getId())) {
                throw new BookAlreadyReturnedException("You have not borrowed this book");
            }
        }

        borrow.setStatus(BorrowStatus.RETURNED);
        borrow.setReturnDate(LocalDate.now());

        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(0);
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        bookRepository.save(book);
        return borrowRepository.save(borrow);
    }

    // ============================
    // My Borrow History (current user)
    // ============================
    public List<Borrow> getMyBorrows(Long userId) {
        return borrowRepository.findAllByUserIdOrderByBorrowDateDesc(userId);
    }

    // ============================
    // All Borrows — Admin Only (paginated)
    // ============================
    public Page<Borrow> getAllBorrows(Pageable pageable) {
        return borrowRepository.findAll(pageable);
    }

    // ============================
    // Borrows by Specific User — Admin Only (paginated)
    // ============================
    public Page<Borrow> getBorrowsByUserId(Long userId, Pageable pageable) {
        // Confirm user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return borrowRepository.findAllByUserId(userId, pageable);
    }

    // ============================
    // Helper: Check if current user is ADMIN
    // ============================
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}