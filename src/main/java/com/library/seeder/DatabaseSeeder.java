package com.library.seeder;

import com.library.model.Book;
import com.library.model.Role;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, BookRepository bookRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedBooks();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@library.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);

            User member = new User();
            member.setName("Omar Nasser");
            member.setEmail("omar@example.com");
            member.setPassword(passwordEncoder.encode("password123"));
            member.setRole(Role.MEMBER);

            userRepository.saveAll(List.of(admin, member));
            System.out.println("✅ Seeded Admin and Member users.");
        }
    }

    private void seedBooks() {
        if (bookRepository.count() == 0) {
            Book b1 = new Book();
            b1.setTitle("Clean Code");
            b1.setAuthor("Robert C. Martin");
            b1.setTotalCopies(5);
            b1.setAvailableCopies(5);
            b1.setCategory("Programming");
            b1.setCoverImageUrl("https://m.media-amazon.com/images/I/41xShlnTZTL._SX376_BO1,204,203,200_.jpg");
            b1.setActive(true);
            
            Book b2 = new Book();
            b2.setTitle("The Pragmatic Programmer");
            b2.setAuthor("Andrew Hunt");
            b2.setTotalCopies(3);
            b2.setAvailableCopies(3);
            b2.setCategory("Programming");
            b2.setCoverImageUrl("https://m.media-amazon.com/images/I/51W1sBPO7tL._SX380_BO1,204,203,200_.jpg");
            b2.setActive(true);
            
            Book b3 = new Book();
            b3.setTitle("Atomic Habits");
            b3.setAuthor("James Clear");
            b3.setTotalCopies(10);
            b3.setAvailableCopies(10);
            b3.setCategory("Self-Help");
            b3.setCoverImageUrl("https://m.media-amazon.com/images/I/513Y5o-DYtL.jpg");
            b3.setActive(true);
            
            Book b4 = new Book();
            b4.setTitle("Deep Work");
            b4.setAuthor("Cal Newport");
            b4.setTotalCopies(4);
            b4.setAvailableCopies(4);
            b4.setCategory("Productivity");
            b4.setCoverImageUrl("https://m.media-amazon.com/images/I/417yGIVTbvL._SX322_BO1,204,203,200_.jpg");
            b4.setActive(true);

            Book b5 = new Book();
            b5.setTitle("Design Patterns");
            b5.setAuthor("Erich Gamma");
            b5.setTotalCopies(2);
            b5.setAvailableCopies(2);
            b5.setCategory("Programming");
            b5.setCoverImageUrl("https://m.media-amazon.com/images/I/51szD9HC9pL._SX395_BO1,204,203,200_.jpg");
            b5.setActive(true);
            
            Book b6 = new Book();
            b6.setTitle("Thinking, Fast and Slow");
            b6.setAuthor("Daniel Kahneman");
            b6.setTotalCopies(6);
            b6.setAvailableCopies(6);
            b6.setCategory("Psychology");
            b6.setCoverImageUrl("https://m.media-amazon.com/images/I/41shqJ-E0zL._SX331_BO1,204,203,200_.jpg");
            b6.setActive(true);

            bookRepository.saveAll(List.of(b1, b2, b3, b4, b5, b6));
            System.out.println("✅ Seeded Initial Book Catalog.");
        }
    }
}
