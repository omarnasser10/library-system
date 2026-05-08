package com.library.seeder;

import com.library.model.*;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import com.library.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, BookRepository bookRepository, 
                          BorrowRepository borrowRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.borrowRepository = borrowRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🌱 Initializing Database Seeding Process...");
        seedUsers();
        seedBooks();
        seedBorrows();
        System.out.println("🏁 Seeding Process Completed.");
    }

    private void seedUsers() {
        if (userRepository.findByEmail("admin@library.com").isEmpty()) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@library.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ Seeded Admin user.");
        }

        if (userRepository.count() <= 1) {
            User u1 = new User();
            u1.setName("Omar Nasser");
            u1.setEmail("omar@example.com");
            u1.setPassword(passwordEncoder.encode("password123"));
            u1.setRole(Role.MEMBER);

            User u2 = new User();
            u2.setName("Ahmed Ali");
            u2.setEmail("ahmed@example.com");
            u2.setPassword(passwordEncoder.encode("password123"));
            u2.setRole(Role.MEMBER);

            User u3 = new User();
            u3.setName("Sara Mohamed");
            u3.setEmail("sara@example.com");
            u3.setPassword(passwordEncoder.encode("password123"));
            u3.setRole(Role.MEMBER);

            User u4 = new User();
            u4.setName("Mariam Hassan");
            u4.setEmail("mariam@example.com");
            u4.setPassword(passwordEncoder.encode("password123"));
            u4.setRole(Role.MEMBER);

            userRepository.saveAll(List.of(u1, u2, u3, u4));
            System.out.println("✅ Seeded 4 Sample Members.");
        }
    }

    private void seedBooks() {
        if (bookRepository.count() == 0) {
            Book b1 = createBook("Clean Code", "Robert C. Martin", 5, "Programming", "https://m.media-amazon.com/images/I/41xShlnTZTL._SX376_BO1,204,203,200_.jpg");
            Book b2 = createBook("The Pragmatic Programmer", "Andrew Hunt", 3, "Programming", "https://m.media-amazon.com/images/I/51W1sBPO7tL._SX380_BO1,204,203,200_.jpg");
            Book b3 = createBook("Atomic Habits", "James Clear", 10, "Self-Help", "https://m.media-amazon.com/images/I/513Y5o-DYtL.jpg");
            Book b4 = createBook("Deep Work", "Cal Newport", 4, "Productivity", "https://m.media-amazon.com/images/I/417yGIVTbvL._SX322_BO1,204,203,200_.jpg");
            Book b5 = createBook("Design Patterns", "Erich Gamma", 2, "Programming", "https://m.media-amazon.com/images/I/51szD9HC9pL._SX395_BO1,204,203,200_.jpg");
            Book b6 = createBook("Thinking, Fast and Slow", "Daniel Kahneman", 6, "Psychology", "https://m.media-amazon.com/images/I/41shqJ-E0zL._SX331_BO1,204,203,200_.jpg");
            Book b7 = createBook("The Alchemist", "Paulo Coelho", 8, "Fiction", "https://m.media-amazon.com/images/I/51Z9n-1-vOL.jpg");
            Book b8 = createBook("Sapiens", "Yuval Noah Harari", 5, "History", "https://m.media-amazon.com/images/I/41yu2qXhXXL._SX324_BO1,204,203,200_.jpg");
            Book b9 = createBook("Introduction to Algorithms", "Thomas H. Cormen", 3, "Programming", "https://m.media-amazon.com/images/I/41SN870m7DL._SX404_BO1,204,203,200_.jpg");
            Book b10 = createBook("The 7 Habits of Highly Effective People", "Stephen Covey", 7, "Self-Help", "https://m.media-amazon.com/images/I/51uIuS9-8KL.jpg");

            bookRepository.saveAll(List.of(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10));
            System.out.println("✅ Seeded 10 Initial Books.");
        }
    }

    private Book createBook(String title, String author, int copies, String category, String coverUrl) {
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor(author);
        b.setTotalCopies(copies);
        b.setAvailableCopies(copies);
        b.setCategory(category);
        b.setCoverImageUrl(coverUrl);
        b.setActive(true);
        return b;
    }

    private void seedBorrows() {
        if (borrowRepository.count() == 0) {
            User omar = userRepository.findByEmail("omar@example.com").orElse(null);
            User ahmed = userRepository.findByEmail("ahmed@example.com").orElse(null);
            List<Book> books = bookRepository.findAll();

            if (omar != null && books.size() >= 4) {
                // Active Borrows for Omar
                createBorrow(omar, books.get(0), LocalDate.now().minusDays(5), BorrowStatus.BORROWED);
                createBorrow(omar, books.get(2), LocalDate.now().minusDays(2), BorrowStatus.BORROWED);
                
                // History for Omar
                createBorrow(omar, books.get(1), LocalDate.now().minusDays(20), BorrowStatus.RETURNED);
                
                // Active for Ahmed
                if (ahmed != null) {
                    createBorrow(ahmed, books.get(3), LocalDate.now().minusDays(1), BorrowStatus.BORROWED);
                }
                
                System.out.println("✅ Seeded Initial Borrow Transactions.");
            }
        }
    }

    private void createBorrow(User user, Book book, LocalDate borrowDate, BorrowStatus status) {
        Borrow b = new Borrow();
        b.setUser(user);
        b.setBook(book);
        b.setBorrowDate(borrowDate);
        b.setDueDate(borrowDate.plusDays(14));
        b.setStatus(status);
        if (status == BorrowStatus.RETURNED) {
            b.setReturnDate(borrowDate.plusDays(7));
        } else {
            // If borrowed, decrement available copies
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
        }
        borrowRepository.save(b);
    }
}
