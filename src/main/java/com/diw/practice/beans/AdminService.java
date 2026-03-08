package com.diw.practice.beans;

import com.diw.practice.model.Book;
import com.diw.practice.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for administrative operations on users and books.
 *
 * <p>This interface defines the basic management operations that the administrator
 * component must expose: user registration and listing, and basic book CRUD.
 * Concrete implementations must handle persistence, validation,
 * and transaction management.</p>
 *
 * <p>General contracts:</p>
 * <ul>
 * <li>Input parameters must not be null unless explicitly stated.</li>
 * <li>Methods returning {@code Optional} use {@code Optional.empty()} to indicate
 * the absence of the resource.</li>
 * <li>Implementations may throw runtime exceptions (e.g., integrity violations,
 * persistence errors). Document and handle such exceptions in the concrete
 * implementation.</li>
 * </ul>
 *
 * 
 */
public interface AdminService {

    /**
     * Registers a new user in the system.
     *
     * <p>The implementation is expected to validate the {@code user} data,
     * check for duplicates according to the application's policy (e.g., by
     * email or username), and persist the entity.</p>
     *
     * @param user the {@link User} object with the data to register; must not be {@code null}
     * @return the persisted {@link User}, normally with an assigned identifier
     * @throws IllegalArgumentException if {@code user} is {@code null} or contains invalid data
     * @throws RuntimeException if a persistence error or other uncontrolled exception occurs
     */
    User registerUser(User user);

    /**
     * Lists all registered users in the system.
     *
     * <p>The result must not be {@code null}. If no users exist, an empty list
     * must be returned.</p>
     *
     * @return non-{@code null} list of {@link User}; may be empty
     * @throws RuntimeException if an error occurs while retrieving the data
     */
    List<User> listUsers();

    /**
     * Registers a new book in the system.
     *
     * <p>The implementation must validate the {@code book} data and persist it.
     * It is expected to return the persisted entity (with id if applicable).</p>
     *
     * @param book the {@link Book} to register; must not be {@code null}
     * @return the persisted {@link Book} with its fields updated (e.g. id)
     * @throws IllegalArgumentException if {@code book} is {@code null} or invalid
     * @throws RuntimeException if an error occurs during persistence
     */
    Book registerBook(Book book);

    /**
     * Updates an existing book.
     *
     * <p>Searches for the book by its identifier {@code bookId} and, if it exists, applies
     * the changes indicated in {@code updatedBook}. It is not assumed that all
     * fields in {@code updatedBook} are complete; the exact semantics
     * (full replacement vs. patch) depend on the concrete implementation.</p>
     *
     * @param bookId        identifier of the book to update; must not be {@code null}
     * @param updatedBook   object with the new book data; must not be {@code null}
     * @return {@link Optional} containing the updated {@link Book} if the entity existed;
     * {@code Optional.empty()} if no book with {@code bookId} was found
     * @throws IllegalArgumentException if {@code bookId} or {@code updatedBook} are {@code null}
     * @throws RuntimeException if an error occurs during the update
     */
    Optional<Book> updateBook(Integer bookId, Book updatedBook);

    /**
     * Deletes a book by its identifier.
     *
     * <p>If the book exists and the deletion is successful, it must return {@code true}.
     * If it does not exist, it must return {@code false}. Implementations must handle
     * referential integrity (e.g., checking for active loans) before deleting.</p>
     *
     * @param bookId identifier of the book to delete; must not be {@code null}
     * @return {@code true} if the book was found and deleted; {@code false} if not found
     * @throws IllegalArgumentException if {@code bookId} is {@code null}
     * @throws RuntimeException if an error occurs in the deletion operation
     */
    boolean deleteBook(Integer bookId);

    /**
     * Lists all available books in the system.
     *
     * <p>The result must not be {@code null}. If there are no registered books, an empty
     * list must be returned.</p>
     *
     * @return non-{@code null} list of {@link Book}; may be empty
     * @throws RuntimeException if an error occurs while retrieving the data
     */
    List<Book> listBooks();
}