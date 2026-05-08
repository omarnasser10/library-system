package com.library.repository;

import com.library.model.Borrow;
import com.library.model.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, BorrowStatus status);

    Optional<Borrow> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, BorrowStatus status);

    Optional<Borrow> findFirstByBookIdAndStatusOrderByBorrowDateAsc(Long bookId, BorrowStatus status);

    // Borrow history for a specific user (newest first)
    List<Borrow> findAllByUserIdOrderByBorrowDateDesc(Long userId);

    // Count by status
    long countByStatus(BorrowStatus status);

    // All borrows with pagination (for admin)
    Page<Borrow> findAll(Pageable pageable);

    // Borrows by a specific user with pagination
    Page<Borrow> findAllByUserId(Long userId, Pageable pageable);
}