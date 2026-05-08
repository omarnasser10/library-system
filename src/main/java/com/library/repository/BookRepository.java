package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // For pagination
    Page<Book> findByActiveTrue(Pageable pageable);

    // For search by title or author
    List<Book> findByActiveTrueAndTitleContainingIgnoreCaseOrActiveTrueAndAuthorContainingIgnoreCase(
            String title, String author
    );
}