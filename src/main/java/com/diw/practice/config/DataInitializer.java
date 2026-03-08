package com.diw.practice.config;

import com.diw.practice.model.Book;
import com.diw.practice.model.User;
import com.diw.practice.repository.BookRepository;
import com.diw.practice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Initializes sample data in the database when the application starts.
 *
 * <p>To avoid duplicates, the process runs only when there are no
 * users or books stored.</p>
 */
@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public DataInitializer(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Creates sample users and books to facilitate manual testing.
     */
    @PostConstruct
    @Transactional
    public void loadInitialData() {
        if (userRepository.count() > 0 || bookRepository.count() > 0) {
            return;
        }

        User admin = new User(null, "Admin User", User.Role.ADMIN);
        User teacher = new User(null, "Teacher User", User.Role.TEACHER);
        User student = new User(null, "Student User", User.Role.STUDENT);

        userRepository.saveAll(List.of(admin, teacher, student));

        Book cleanCode = new Book(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008,
                "Prentice Hall",
                Book.Status.AVAILABLE
        );

        Book ddd = new Book(
                "Domain-Driven Design",
                "Eric Evans",
                "978-0321125217",
                2003,
                "Addison-Wesley",
                Book.Status.BORROWED
        );

        Book springInAction = new Book(
                "Spring in Action",
                "Craig Walls",
                "978-1617294945",
                2018,
                "Manning",
                Book.Status.RESERVED
        );

        // Link the borrowed and reserved books to specific users to reflect real states.
        ddd.setBorrowedBy(student);
        student.getBooks().add(ddd);

        springInAction.setBorrowedBy(teacher);
        teacher.getBooks().add(springInAction);

        bookRepository.saveAll(List.of(cleanCode, ddd, springInAction));
        userRepository.saveAll(List.of(student, teacher));
    }
}