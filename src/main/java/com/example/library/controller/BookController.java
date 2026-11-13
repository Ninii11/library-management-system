
package com.example.library.controller;

import com.example.library.dto.request.BookRequest;
import com.example.library.dto.response.BookResponse;
import com.example.library.service.BookService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Page<BookResponse> getBooks(
            @PageableDefault(size = 10, sort = "title")
            Pageable pageable) {
        return bookService.getBooks(pageable);
    }

    @GetMapping("/{id}")
    public BookResponse getBook(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(
            @Valid @RequestBody BookRequest request) {
        return bookService.createBook(request);
    }

    @PostMapping(
            value = "/{id}/cover",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public BookResponse uploadCover(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        return bookService.uploadBookCover(id, file);
    }
    @PutMapping("/{id}")
    public BookResponse updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        return bookService.updateBook(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<BookResponse> searchBooks(@RequestParam String title) {
        return bookService.searchBooks(title);
    }

    @GetMapping("/cheap")
    public List<BookResponse> cheapBooks(@RequestParam double price) {
        return bookService.findCheapBooks(price);
    }

    @GetMapping("/price-range")
    public List<BookResponse> priceRange(
            @RequestParam double min,
            @RequestParam double max) {
        return bookService.findBooksByPriceRange(min, max);
    }

    @GetMapping("/category")
    public List<BookResponse> byCategory(@RequestParam String name) {
        return bookService.findBooksByCategory(name);
    }
}
