package com.diw.practice.beans;

import com.diw.practice.model.Book;
import com.diw.practice.model.User;
import com.diw.practice.repository.BookRepository;
import com.diw.practice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the administrative service responsible for CRUD operations
 * on {@link User} and {@link Book} entities.
 *
 * <p>This class delegates persistence to the {@link UserRepository} and
 * {@link BookRepository} instances injected by Spring.</p>
 *
 * 
 * @since 1.0
 */
@Service
public class AdminServiceImpl implements AdminService {

    /**
     * Repository for operations on {@link User}.
     */
    private final UserRepository userRepository;

    /**
     * Repository for operations on {@link Book}.
     */
    private final BookRepository bookRepository;

    /**
     * Constructs a new instance of {@code AdminServiceImpl} with the
     * required injected repositories.
     *
     * @param userRepository repository for user management; must not be {@code null}
     * @param bookRepository repository for book management; must not be {@code null}
     */
    @Autowired
    public AdminServiceImpl(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "User repository is mandatory");
        this.bookRepository = Objects.requireNonNull(bookRepository, "Book repository is mandatory");
    }

    /**
     * Registers a new user in persistence.
     *
     * <p>Uses {@link UserRepository#save(Object)} to store the user.
     * If the repository throws a data access exception, it will propagate to the caller.</p>
     *
     * @param user {@link User} entity to register; expected to contain necessary data
     * @return the persisted instance of {@link User} (may include generated fields like id)
     */
    @Override
    public User registerUser(User user) {
        Objects.requireNonNull(user, "User cannot be null");

        // When creating a user via POST, an id=0 might be sent from the client.
        // We force the identifier to null to prevent Hibernate from attempting
        // a merge on a non-existent row, which causes a StaleObjectStateException.
        user.setId(null);

        return userRepository.save(user);
    }

    /**
     * Returns a list of all stored users.
     *
     * @return list of {@link User} instances; never {@code null} (may be empty)
     */
    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    /**
     * Registers a new book in persistence.
     *
     * <p>If the {@code status} field of the provided object is {@code null},
     * it defaults to {@link Book.Status#AVAILABLE} before persisting.</p>
     *
     * @param book {@link Book} entity to register
     * @return the persisted instance of {@link Book} (may contain generated fields like id)
     */
    @Override
    public Book registerBook(Book book) {
        Objects.requireNonNull(book, "Book cannot be null");

        // We force the identifier to null to prevent Hibernate merge issues on creation.
        book.setId(null);

        if (book.getStatus() == null) {
            book.setStatus(Book.Status.AVAILABLE);
        }
        return bookRepository.save(book);
    }

    /**
     * Updates an existing book's data with the provided values.
     *
     * <p>Searches for the book by its identifier; if it exists, updates fields
     * title, author, ISBN, publication year, publisher and, if provided,
     * the book status. Persists changes and returns the updated book.</p>
     *
     * @param bookId      identifier of the book to update
     * @param updatedBook {@link Book} object containing the new values
     * @return {@link Optional} containing the updated book if found,
     * or {@link Optional#empty()} if no book exists with the given identifier
     */
    @Override
    public Optional<Book> updateBook(Integer bookId, Book updatedBook) {
        Objects.requireNonNull(bookId, "Book identifier cannot be null");
        Objects.requireNonNull(updatedBook, "Updated book cannot be null");

        return bookRepository.findById(bookId).map(existingBook -> {

            if (updatedBook.getTitle() != null) {
                existingBook.setTitle(updatedBook.getTitle());
            }
            if (updatedBook.getAuthor() != null) {
                existingBook.setAuthor(updatedBook.getAuthor());
            }
            if (updatedBook.getIsbn() != null) {
                existingBook.setIsbn(updatedBook.getIsbn());
            }
            if (updatedBook.getPublicationYear() != null) {
                existingBook.setPublicationYear(updatedBook.getPublicationYear());
            }
            if (updatedBook.getPublisher() != null) {
                existingBook.setPublisher(updatedBook.getPublisher());
            }
            if (updatedBook.getStatus() != null) {
                existingBook.setStatus(updatedBook.getStatus());
            }

            return bookRepository.save(existingBook);
        });
    }

    /**
     * Deletes a book by its identifier.
     *
     * <p>If the book exists, it is deleted and the method returns {@code true}.
     * If it does not exist, it returns {@code false}.</p>
     *
     * @param bookId identifier of the book to delete
     * @return {@code true} if the book was found and deleted; {@code false} otherwise
     */
    @Override
    public boolean deleteBook(Integer bookId) {
        Objects.requireNonNull(bookId, "Book identifier cannot be null");
        return bookRepository.findById(bookId).map(book -> {
            bookRepository.delete(book);
            return true;
        }).orElse(false);
    }

    /**
     * Retrieves a list of all stored books.
     *
     * @return list of {@link Book} instances; never {@code null} (may be empty)
     */
    @Override
    public List<Book> listBooks() {
        return bookRepository.findAll();
    }
}