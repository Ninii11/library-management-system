
package com.example.library.service;

import com.example.library.dto.request.AuthorRequest;
import com.example.library.dto.response.AuthorResponse;
import com.example.library.entity.Author;
import com.example.library.repository.AuthorRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    public AuthorResponse createAuthor(AuthorRequest request) {
        Author author = new Author(request.name());
        return toResponse(authorRepository.save(author));
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(Long id) {
        return toResponse(findAuthor(id));
    }

    @Transactional
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        Author author = findAuthor(id);
        author.setName(request.name());
        return toResponse(authorRepository.save(author));
    }

    @Transactional
    public void deleteAuthor(Long id) {
        Author author = findAuthor(id);
        if (!author.getBooks().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete an author with books");
        }
        authorRepository.delete(author);
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> searchAuthors(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> findAuthorsWithBooks() {
        return authorRepository.findAuthorsWithBooks()
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> findAuthorsWithMoreThanBooks(long count) {
        return authorRepository.findAuthorsWithMoreThanBooks(count)
                .stream().map(this::toResponse).toList();
    }

    private Author findAuthor(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Author not found"));
    }

    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(author.getId(), author.getName());
    }
}
