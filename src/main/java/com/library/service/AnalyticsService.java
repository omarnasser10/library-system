package com.library.service;

import com.library.dto.AnalyticsResponse;
import com.library.model.BorrowStatus;
import com.library.model.Role;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import com.library.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;

    public AnalyticsService(BookRepository bookRepository, UserRepository userRepository, BorrowRepository borrowRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.borrowRepository = borrowRepository;
    }

    public AnalyticsResponse getSummary() {
        long totalBooks = bookRepository.countByActiveTrue();
        long totalUsers = userRepository.countByRole(Role.MEMBER);
        long activeBorrows = borrowRepository.countByStatus(BorrowStatus.BORROWED);

        return new AnalyticsResponse(totalBooks, totalUsers, activeBorrows);
    }
}
