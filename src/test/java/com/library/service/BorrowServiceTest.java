package com.library.service;

import com.library.exception.*;
import com.library.model.*;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock private BorrowRepository borrowRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookRepository bookRepository;

    @InjectMocks
    private BorrowService borrowService;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User("Omar", "omar@test.com", "hashed", Role.MEMBER);
        user.setId(1L);

        book = new Book();
        book.setId(10L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setTotalCopies(3);
        book.setAvailableCopies(3);
        book.setActive(true);
    }

    // =============================================
    // borrowBook — Success
    // =============================================
    @Test
    void borrowBook_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(borrowRepository.existsByUserIdAndBookIdAndStatus(1L, 10L, BorrowStatus.BORROWED))
                .thenReturn(false);

        Borrow savedBorrow = new Borrow(user, book, LocalDate.now(), LocalDate.now().plusDays(14), null, BorrowStatus.BORROWED);
        when(borrowRepository.save(any(Borrow.class))).thenReturn(savedBorrow);

        Borrow result = borrowService.borrowBook(1L, 10L);

        assertThat(result.getStatus()).isEqualTo(BorrowStatus.BORROWED);
        assertThat(book.getAvailableCopies()).isEqualTo(2); // decreased by 1
        verify(bookRepository).save(book);
        verify(borrowRepository).save(any(Borrow.class));
    }

    // =============================================
    // borrowBook — Book not found
    // =============================================
    @Test
    void borrowBook_throwsWhenBookNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 10L))
                .isInstanceOf(BookNotFoundException.class);
    }

    // =============================================
    // borrowBook — Already borrowed
    // =============================================
    @Test
    void borrowBook_throwsWhenAlreadyBorrowed() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(borrowRepository.existsByUserIdAndBookIdAndStatus(1L, 10L, BorrowStatus.BORROWED))
                .thenReturn(true);

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 10L))
                .isInstanceOf(BookAlreadyBorrowedException.class);
    }

    // =============================================
    // borrowBook — No available copies
    // =============================================
    @Test
    void borrowBook_throwsWhenNoCopiesAvailable() {
        book.setAvailableCopies(0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(borrowRepository.existsByUserIdAndBookIdAndStatus(1L, 10L, BorrowStatus.BORROWED))
                .thenReturn(false);

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 10L))
                .isInstanceOf(NoAvailableCopiesException.class);
    }

    // =============================================
    // returnBook — Success
    // =============================================
    @Test
    void returnBook_success() {
        book.setAvailableCopies(2); // 1 copy is out

        Borrow activeBorrow = new Borrow(user, book, LocalDate.now().minusDays(3), LocalDate.now().plusDays(11), null, BorrowStatus.BORROWED);
        activeBorrow.setId(100L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(borrowRepository.findByUserIdAndBookIdAndStatus(1L, 10L, BorrowStatus.BORROWED))
                .thenReturn(Optional.of(activeBorrow));
        when(borrowRepository.save(any(Borrow.class))).thenReturn(activeBorrow);

        Borrow result = borrowService.returnBook(1L, 10L);

        assertThat(result.getStatus()).isEqualTo(BorrowStatus.RETURNED);
        assertThat(book.getAvailableCopies()).isEqualTo(3); // restored
        verify(bookRepository).save(book);
    }

    // =============================================
    // returnBook — Not borrowed
    // =============================================
    @Test
    void returnBook_throwsWhenNotBorrowed() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(borrowRepository.findByUserIdAndBookIdAndStatus(1L, 10L, BorrowStatus.BORROWED))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowService.returnBook(1L, 10L))
                .isInstanceOf(BookAlreadyReturnedException.class);
    }

    // =============================================
    // getMyBorrows
    // =============================================
    @Test
    void getMyBorrows_returnsUserHistory() {
        Borrow b1 = new Borrow(user, book, LocalDate.now(), LocalDate.now().plusDays(14), null, BorrowStatus.BORROWED);
        when(borrowRepository.findAllByUserIdOrderByBorrowDateDesc(1L)).thenReturn(List.of(b1));

        List<Borrow> result = borrowService.getMyBorrows(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(BorrowStatus.BORROWED);
    }

    // =============================================
    // getAllBorrows — Admin
    // =============================================
    @Test
    void getAllBorrows_returnsPagedResults() {
        Borrow b1 = new Borrow(user, book, LocalDate.now(), LocalDate.now().plusDays(14), null, BorrowStatus.BORROWED);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Borrow> page = new PageImpl<>(List.of(b1));
        when(borrowRepository.findAll(pageable)).thenReturn(page);

        Page<Borrow> result = borrowService.getAllBorrows(pageable);

        assertThat(result.getContent()).hasSize(1);
    }
}
