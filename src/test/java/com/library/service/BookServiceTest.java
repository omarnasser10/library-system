package com.library.service;

import com.library.dto.AddBookRequest;
import com.library.dto.UpdateBookRequest;
import com.library.exception.BookCannotBeDeletedException;
import com.library.exception.BookNotFoundException;
import com.library.model.Book;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book activeBook;

    @BeforeEach
    void setUp() {
        activeBook = new Book();
        activeBook.setId(1L);
        activeBook.setTitle("Clean Code");
        activeBook.setAuthor("Robert Martin");
        activeBook.setTotalCopies(5);
        activeBook.setAvailableCopies(5);
        activeBook.setActive(true);
    }

    // =============================================
    // viewAllBooks
    // =============================================
    @Test
    void viewAllBooks_returnsActiveBooksPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(activeBook));
        when(bookRepository.findByActiveTrue(pageable)).thenReturn(page);

        Page<Book> result = bookService.viewAllBooks(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Clean Code");
    }

    // =============================================
    // searchBooks
    // =============================================
    @Test
    void searchBooks_byTitle_returnsMatchingBooks() {
        when(bookRepository
                .findByActiveTrueAndTitleContainingIgnoreCaseOrActiveTrueAndAuthorContainingIgnoreCase(
                        "clean", "clean"))
                .thenReturn(List.of(activeBook));

        List<Book> result = bookService.searchBooks("clean");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Clean Code");
    }

    // =============================================
    // addBook
    // =============================================
    @Test
    void addBook_savesAndReturnsBook() {
        AddBookRequest request = new AddBookRequest("Clean Code", "Robert Martin", 5, "Programming", "http://example.com/cover.jpg", "http://example.com/book.pdf");
        when(bookRepository.save(any(Book.class))).thenReturn(activeBook);

        Book result = bookService.addBook(request);

        assertThat(result.getTitle()).isEqualTo("Clean Code");
        assertThat(result.getAvailableCopies()).isEqualTo(5);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    // =============================================
    // updateBook
    // =============================================
    @Test
    void updateBook_updatesTitle() {
        UpdateBookRequest request = new UpdateBookRequest("New Title", null, null, null, null, null);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(activeBook));
        when(bookRepository.save(any(Book.class))).thenReturn(activeBook);

        Book result = bookService.updateBook(1L, request);

        assertThat(result.getTitle()).isEqualTo("New Title");
    }

    @Test
    void updateBook_throwsWhenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(99L, new UpdateBookRequest()))
                .isInstanceOf(BookNotFoundException.class);
    }

    // =============================================
    // deleteBook
    // =============================================
    @Test
    void deleteBook_softDeletesWhenAllCopiesAvailable() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(activeBook));

        bookService.deleteBook(1L);

        assertThat(activeBook.getActive()).isFalse();
        verify(bookRepository).save(activeBook);
    }

    @Test
    void deleteBook_throwsWhenBookHasBorrowedCopies() {
        activeBook.setAvailableCopies(3); // 2 copies are borrowed
        when(bookRepository.findById(1L)).thenReturn(Optional.of(activeBook));

        assertThatThrownBy(() -> bookService.deleteBook(1L))
                .isInstanceOf(BookCannotBeDeletedException.class)
                .hasMessageContaining("borrowed copies");
    }

    @Test
    void deleteBook_throwsWhenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(99L))
                .isInstanceOf(BookNotFoundException.class);
    }
}
