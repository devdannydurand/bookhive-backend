package com.diw.practice.controller;

import com.diw.practice.beans.AdminService;
import com.diw.practice.model.Book;
import com.diw.practice.model.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.headers.Header;

/**
 * Controller for administrative operations on users and books.
 * All routes are protected and require the 'ADMIN' role.
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Administrative operations for users and books")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Lists all registered users.
     *
     * @return list of users
     */
    @GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List users", description = "Returns the list of all registered users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User list successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public List<User> listUsers() {
        return adminService.listUsers();
    }

    /**
     * Creates a new user.
     *
     * @param user user object to create
     * @return created user with 201 status
     */
    @PostMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create user", description = "Creates a new user in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)),
                    headers = @Header(name = "Location", description = "URI of the created resource", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<User> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User to create", required = true)
            @RequestBody User user) {
        User created = adminService.registerUser(user);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * Lists all books.
     *
     * @return list of books
     */
    @GetMapping(path = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List books", description = "Returns the list of all books")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book list successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Book.class)))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public List<Book> listBooks() {
        return adminService.listBooks();
    }

    /**
     * Creates a new book.
     *
     * @param book book to create
     * @return created book with 201 status
     */
    @PostMapping(path = "/books", consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create book", description = "Creates a new book in the catalog")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)),
                    headers = @Header(name = "Location", description = "URI of the created resource", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Book> createBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Book to create", required = true)
            @RequestBody Book book) {
        return ResponseEntity.status(201).body(adminService.registerBook(book));
    }

    /**
     * Updates an existing book.
     *
     * @param bookId      ID of the book to update
     * @param updatedBook updated book data
     * @return updated book or 404 if not found
     */
    @PutMapping(path = "/books/{bookId}", consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update book", description = "Updates the data of an existing book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Book> updateBook(
            @Parameter(description = "ID of the book to update", required = true) @PathVariable Integer bookId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated book data", required = true)
            @RequestBody Book updatedBook
    ) {
        return adminService.updateBook(bookId, updatedBook)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a book by its ID.
     *
     * @param bookId ID of the book to delete
     * @return 204 if deleted, 404 if not found
     */
    @DeleteMapping(path = "/books/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete book", description = "Deletes a book by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID of the book to delete", required = true) @PathVariable Integer bookId) {
        return adminService.deleteBook(bookId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}