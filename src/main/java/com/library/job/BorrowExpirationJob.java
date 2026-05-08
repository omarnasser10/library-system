package com.library.job;

import com.library.model.Book;
import com.library.model.Borrow;
import com.library.model.BorrowStatus;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class BorrowExpirationJob {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;

    public BorrowExpirationJob(BorrowRepository borrowRepository, BookRepository bookRepository) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
    }

    // Run every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoReturnExpiredBorrows() {
        LocalDate today = LocalDate.now();
        List<Borrow> expiredBorrows = borrowRepository.findByStatusAndDueDateBefore(BorrowStatus.BORROWED, today);

        for (Borrow borrow : expiredBorrows) {
            borrow.setStatus(BorrowStatus.RETURNED);
            borrow.setReturnDate(today);

            Book book = borrow.getBook();
            if (book.getAvailableCopies() == null) {
                book.setAvailableCopies(0);
            }
            book.setAvailableCopies(book.getAvailableCopies() + 1);

            bookRepository.save(book);
            borrowRepository.save(borrow);
        }

        if (!expiredBorrows.isEmpty()) {
            System.out.println("Auto-returned " + expiredBorrows.size() + " expired borrows.");
        }
    }
}
