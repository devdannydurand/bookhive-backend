package com.diw.practice.controller;

import com.diw.practice.beans.UserService;
import com.diw.practice.model.Book;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import io.swagger.v3.oas.annotations.headers.Header;

/**
 * Controller for operations available to users with the 'USER' role.
 * Handles book listings, loans, and returns.
 */
@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('USER')")
@Tag(name = "User", description = "Operations for users (loans, returns, listings)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns the list of books available for loan.
     *
     * @return list of available books
     */
    @GetMapping(path = "/books/available", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List available books", description = "Returns the list of books that are available for loan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book list successfully retrieved",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Book.class)))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public List<Book> availableBooks() {
        return userService.availableBooks();
    }

    /**
     * Retrieves the loans for a user.
     *
     * @param userId user ID
     * @return list of loaned books or 404 if no loans found
     */
    @GetMapping(path = "/{userId}/loans", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List user loans", description = "Returns the books currently loaned to a given user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loans successfully retrieved",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Book.class)))),
            @ApiResponse(responseCode = "404", description = "No loans found for the user", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<List<Book>> userLoans(
            @Parameter(description = "User ID", required = true) @PathVariable Integer userId) {
        List<Book> loans = userService.userLoans(userId);
        if (loans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loans);
    }

    /**
     * Requests a book loan for a user.
     *
     * @param userId ID of the requesting user
     * @param bookId   ID of the book to request
     * @return requested book with 201 status or 400 if not possible
     */
    @PostMapping(path = "/{userId}/loans/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Request loan", description = "Requests a book loan for the specified user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Loan successfully requested",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)),
                    headers = @Header(name = "Location", description = "URI of the created resource", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid request or book not available", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Book> requestLoan(
            @Parameter(description = "ID of the requesting user", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID of the book to request", required = true) @PathVariable Integer bookId
    ) {
        return userService.requestLoan(userId, bookId)
                .map(book -> ResponseEntity.status(201).body(book))
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * Returns a book loaned by a user.
     *
     * @param userId ID of the returning user
     * @param bookId   ID of the book to return
     * @return returned book or 400 if return is invalid
     */
    @PostMapping(path = "/{userId}/returns/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Return loan", description = "Registers the return of a book by the user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Return successfully processed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "400", description = "Invalid return", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Book> returnLoan(
            @Parameter(description = "ID of the returning user", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID of the book to return", required = true) @PathVariable Integer bookId
    ) {
        return userService.returnLoan(userId, bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
}