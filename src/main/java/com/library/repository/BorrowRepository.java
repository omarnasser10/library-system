package com.library.repository;

import com.library.model.Borrow;
import com.library.model.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    boolean existsByUserIdAndBookIdAndStatus(
            Long userId,
            Long bookId,
            BorrowStatus status
    );
    Optional<Borrow> findByUserIdAndBookIdAndStatus(
            Long userId,
            Long bookId,
            BorrowStatus status
    );
    Optional<Borrow> findFirstByBookIdAndStatusOrderByBorrowDateAsc(
            Long bookId,
            BorrowStatus status
    );
}