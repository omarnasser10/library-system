package com.library.dto;

import com.library.model.BorrowStatus;

import java.time.LocalDate;

public class BorrowResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private BorrowStatus status;

    public BorrowResponse(Long id, Long bookId, String bookTitle, Long userId,
                          LocalDate borrowDate, LocalDate returnDate, BorrowStatus status) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public BorrowStatus getStatus() {
        return status;
    }
}