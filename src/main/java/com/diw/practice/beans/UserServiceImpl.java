package com.diw.practice.beans;

import com.diw.practice.model.Book;
import com.diw.practice.model.User;
import com.diw.practice.repository.BookRepository;
import com.diw.practice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of {@code UserService} that manages operations related to
 * users and books in the repository.
 *
 * <p>This class provides methods to:
 * <ul>
 * <li>List available books.</li>
 * <li>Get the loans (books) of a user.</li>
 * <li>Request a book loan by a user.</li>
 * <li>Return a book borrowed by a user.</li>
 * </ul>
 *
 * <p>Operations that modify entities persist changes through the
 * {@link UserRepository} and {@link BookRepository} repositories.
 *
 * @see UserService
 * @see UserRepository
 * @see BookRepository
 * @since 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * User repository injected for searches and persistence.
     */
    private final UserRepository userRepository;

    /**
     * Book repository injected for searches and persistence.
     */
    private final BookRepository bookRepository;

    /**
     * Creates a new instance of {@code UserServiceImpl} with the required repositories.
     *
     * @param userRepository repository used for operations with {@link User}; must not be {@code null}.
     * @param bookRepository   repository used for operations with {@link Book}; must not be {@code null}.
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Retrieves the list of books whose status is {@link Book.Status#AVAILABLE}.
     *
     * <p>This method delegates the query to the {@link BookRepository}.
     *
     * @return list of available books; if none, an empty list is returned.
     */
    @Override
    public List<Book> availableBooks() {
        return bookRepository.findByStatus(Book.Status.AVAILABLE);
    }

    /**
     * Obtains the list of books currently loaned to a specific user.
     *
     * <p>If the user does not exist, an empty list is returned.
     *
     * @param userId identifier of the user whose loan list is requested; must not be {@code null}.
     * @return list of {@link Book} associated with the user, or an empty list if the user does not exist.
     */
    @Override
    public List<Book> userLoans(Integer userId) {
        Objects.requireNonNull(userId, "User identifier cannot be null");

        return userRepository.findById(userId)
                .map(User::getBooks)
                .orElse(Collections.emptyList());
    }

    /**
     * Requests a book loan for a user.
     *
     * <p>Flow:
     * <ol>
     * <li>Verifies that the user and the book exist.</li>
     * <li>Verifies that the book is in {@link Book.Status#AVAILABLE} status.</li>
     * <li>Updates the book status to {@link Book.Status#BORROWED}, assigns the user
     * as the borrower, and adds the book to the user's book collection.</li>
     * <li>Persists changes in both repositories and returns the updated book.</li>
     * </ol>
     *
     * <p>If the user or the book do not exist, or the book is not available, {@link Optional#empty()} is returned.
     *
     * @param userId identifier of the user requesting the loan.
     * @param bookId   identifier of the requested book.
     * @return {@link Optional} with the loaned {@link Book} after the operation, or {@code Optional.empty()} if it could not be performed.
     */
    @Override
    public Optional<Book> requestLoan(Integer userId, Integer bookId) {
        Objects.requireNonNull(userId, "User identifier cannot be null");
        Objects.requireNonNull(bookId, "Book identifier cannot be null");

        Optional<User> user = userRepository.findById(userId);
        Optional<Book> book = bookRepository.findById(bookId);

        if (user.isEmpty() || book.isEmpty()) {
            return Optional.empty();
        }

        Book requestedBook = book.get();
        if (requestedBook.getStatus() != Book.Status.AVAILABLE) {
            return Optional.empty();
        }

        requestedBook.setStatus(Book.Status.BORROWED);
        requestedBook.setBorrowedBy(user.get());
        user.get().getBooks().add(requestedBook);

        bookRepository.save(requestedBook);
        userRepository.save(user.get());

        return Optional.of(requestedBook);
    }

    /**
     * Processes the return of a book borrowed by a user.
     *
     * <p>Flow:
     * <ol>
     * <li>Verifies that the user and the book exist.</li>
     * <li>Verifies that the book is actually loaned to the specified user.</li>
     * <li>Updates the book status to {@link Book.Status#AVAILABLE}, removes the reference
     * to the borrower, and removes the book from the user's loan collection.</li>
     * <li>Persists changes in both repositories and returns the updated book.</li>
     * </ol>
     *
     * <p>If the user or book do not exist, or if the book is not loaned to the specified user,
     * {@link Optional#empty()} is returned.
     *
     * @param userId identifier of the user making the return.
     * @param bookId   identifier of the book being returned.
     * @return {@link Optional} with the updated {@link Book} after the return, or {@code Optional.empty()} if it could not be processed.
     */
    @Override
    public Optional<Book> returnLoan(Integer userId, Integer bookId) {
        Objects.requireNonNull(userId, "User identifier cannot be null");
        Objects.requireNonNull(bookId, "Book identifier cannot be null");

        Optional<User> user = userRepository.findById(userId);
        Optional<Book> book = bookRepository.findById(bookId);

        if (user.isEmpty() || book.isEmpty()) {
            return Optional.empty();
        }

        Book borrowedBook = book.get();
        if (borrowedBook.getBorrowedBy() == null || !borrowedBook.getBorrowedBy().getId().equals(userId)) {
            return Optional.empty();
        }

        borrowedBook.setStatus(Book.Status.AVAILABLE);
        borrowedBook.setBorrowedBy(null);
        user.get().getBooks().removeIf(l -> l.getId().equals(bookId));

        bookRepository.save(borrowedBook);
        userRepository.save(user.get());

        return Optional.of(borrowedBook);
    }
}