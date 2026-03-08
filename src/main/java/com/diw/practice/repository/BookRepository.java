package com.diw.practice.repository;

import com.diw.practice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    /**
     * Finds books by their current status (AVAILABLE, BORROWED, RESERVED).
     * * @param status the status to filter by
     * @return a list of books matching the given status
     */
    List<Book> findByStatus(Book.Status status);
}