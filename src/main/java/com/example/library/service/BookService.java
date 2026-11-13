
package com.example.library.service;

import com.example.library.dto.request.BookRequest;
import com.example.library.dto.response.BookResponse;
import com.example.library.entity.*;
import com.example.library.repository.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final LoanRepository loanRepository;
    private final FileStorageService fileStorageService;

    // Constructor Injection
    public BookService(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            CategoryRepository categoryRepository,
            PublisherRepository publisherRepository,
            LoanRepository loanRepository,
            FileStorageService fileStorageService) {

        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
        this.loanRepository = loanRepository;
        this.fileStorageService = fileStorageService;
    }

    // GET ALL BOOKS - Pagination and Sorting
    @Transactional(readOnly = true)
    public Page<BookResponse> getBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(this::toResponse);
    }

    // GET BOOK BY ID
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        return toResponse(findBook(id));
    }

    // CREATE BOOK
    @Transactional
    public BookResponse createBook(BookRequest request) {
        Book book = new Book();

        applyRequest(book, request);

        return toResponse(bookRepository.save(book));
    }

    // UPDATE BOOK
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = findBook(id);

        applyRequest(book, request);

        return toResponse(bookRepository.save(book));
    }

    // DELETE BOOK
    @Transactional
    public void deleteBook(Long id) {
        Book book = findBook(id);

        // Do not delete books that have loan history
        if (!loanRepository.findByBookId(id).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete book with loan history");
        }

        bookRepository.delete(book);
    }

    // SEARCH BOOKS BY TITLE
    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String title) {
        return bookRepository
                .findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // FIND BOOKS CHEAPER THAN GIVEN PRICE
    @Transactional(readOnly = true)
    public List<BookResponse> findCheapBooks(double price) {
        return bookRepository
                .findByPriceLessThan(price)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // FIND BOOKS WITHIN PRICE RANGE
    @Transactional(readOnly = true)
    public List<BookResponse> findBooksByPriceRange(
            double min, double max) {

        if (min > max) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum price cannot exceed maximum price");
        }

        return bookRepository
                .findBooksByPriceRange(min, max)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // FIND BOOKS BY CATEGORY
    @Transactional(readOnly = true)
    public List<BookResponse> findBooksByCategory(String name) {
        return bookRepository
                .findBooksByCategory(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // FIND BOOK ENTITY BY ID
    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book not found"));
    }

    // APPLY REQUEST DATA TO BOOK
    private void applyRequest(Book book, BookRequest request) {

        // Find author
        Author author = authorRepository
                .findById(request.authorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Author not found"));

        // Find categories
        Set<Category> categories = new HashSet<>(
                categoryRepository.findAllById(
                        request.categoryIds()));

        // Validate category IDs
        if (categories.size() != request.categoryIds().size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "One or more categories do not exist");
        }

        // Find publisher (optional)
        Publisher publisher = null;

        if (request.publisherId() != null) {
            publisher = publisherRepository
                    .findById(request.publisherId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Publisher not found"));
        }

        // Update book fields
        book.setTitle(request.title());
        book.setPrice(request.price());
        book.setAuthor(author);
        book.setCategories(categories);
        book.setPublisher(publisher);
    }

    // CONVERT BOOK ENTITY TO RESPONSE DTO
    private BookResponse toResponse(Book book) {

        Set<String> categoryNames = book.getCategories()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toSet());

        Publisher publisher = book.getPublisher();

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getPrice(),
                book.getCoverImagePath(),
                book.getAuthor().getId(),
                book.getAuthor().getName(),
                categoryNames,
                publisher != null ? publisher.getId() : null,
                publisher != null ? publisher.getName() : null
        );
    }

    // UPLOAD BOOK COVER IMAGE
    @Transactional
    public BookResponse uploadBookCover(
            Long bookId, MultipartFile file) {

        Book book = findBook(bookId);

        String oldPath = book.getCoverImagePath();
        String newPath = fileStorageService.saveFile(file);

        try {
            book.setCoverImagePath(newPath);

            Book saved = bookRepository.saveAndFlush(book);

            if (oldPath != null) {
                fileStorageService.deleteFile(oldPath);
            }

            return toResponse(saved);

        } catch (RuntimeException e) {
            fileStorageService.deleteFile(newPath);
            throw e;
        }
    }
}
