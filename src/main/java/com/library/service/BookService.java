package com.library.service;

import com.library.dto.AddBookRequest;
import com.library.exception.BookCannotBeDeletedException;
import com.library.exception.BookNotFoundException;
import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    public final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> viewAllBooks() {
        return bookRepository.findByActiveTrue();
    }

    public Book addBook(AddBookRequest request){

        Book book = new Book();

        book.setTitle(request.getTitle());

        book.setAuthor(request.getAuthor());

        book.setTotalCopies(request.getTotalCopies());

        book.setAvailableCopies(request.getTotalCopies());

        book.setActive(true);

        return bookRepository.save(book);
    }

    public void deleteBook(Long id){

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new BookNotFoundException(
                                "Book not found"
                        ));

        if (!book.getAvailableCopies()
                .equals(book.getTotalCopies())) {

            throw new BookCannotBeDeletedException(
                    "Cannot delete borrowed book"
            );
        }

        book.setActive(false);

        bookRepository.save(book);
    }
}
