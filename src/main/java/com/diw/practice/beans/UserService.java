package com.diw.practice.beans;

import com.diw.practice.model.Book;

import java.util.List;
import java.util.Optional;

/**
 * Service for operations related to users and book loans.
 *
 * <p>Defines the basic operations that implementations must offer:
 * listing available books, listing a user's loans, requesting a loan,
 * and returning a loan.</p>
 *
 * @see com.diw.practice.model.Book
 * @since 1.0
 */
public interface UserService {

    /**
     * Retrieves all books currently available for loan.
     *
     * <p>The returned list must not be {@code null}; it may be empty if no books are available.</p>
     *
     * @return list of books available for loan
     */
    List<Book> availableBooks();

    /**
     * Retrieves the books currently borrowed by a user identified by {@code userId}.
     *
     * <p>If the user has no loans, an empty list must be returned.</p>
     *
     * @param userId identifier of the user whose loans are to be consulted; must not be {@code null}
     * @return list of books loaned to the user (empty if they have no loans)
     * @throws IllegalArgumentException if {@code userId} is {@code null}
     */
    List<Book> userLoans(Integer userId);

    /**
     * Requests a book loan for a user.
     *
     * <p>If the request is processed successfully and the book is loaned, an
     * {@link Optional} containing the loaned {@link Book} is returned. If the request cannot
     * be completed (e.g., the book does not exist, is not available, or the user
     * cannot take it), {@link Optional#empty()} is returned.</p>
     *
     * @param userId identifier of the requesting user; must not be {@code null}
     * @param bookId identifier of the book to request; must not be {@code null}
     * @return {@code Optional} with the loaned book if the operation was successful, or {@code Optional.empty()} otherwise
     * @throws IllegalArgumentException if {@code userId} or {@code bookId} are {@code null}
     */
    Optional<Book> requestLoan(Integer userId, Integer bookId);

    /**
     * Returns a book that was previously loaned to a user.
     *
     * <p>If the return is processed successfully and the loan is closed, an
     * {@link Optional} containing the returned {@link Book} is returned. If no such
     * loan existed or the return cannot be performed, {@link Optional#empty()} is returned.</p>
     *
     * @param userId identifier of the user returning the book; must not be {@code null}
     * @param bookId identifier of the book to return; must not be {@code null}
     * @return {@code Optional} with the returned book if the operation was successful, or {@code Optional.empty()} otherwise
     * @throws IllegalArgumentException if {@code userId} or {@code bookId} are {@code null}
     */
    Optional<Book> returnLoan(Integer userId, Integer bookId);
}