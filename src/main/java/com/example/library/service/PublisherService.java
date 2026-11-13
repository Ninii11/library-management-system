
package com.example.library.service;

import com.example.library.dto.request.PublisherRequest;
import com.example.library.dto.response.PublisherResponse;
import com.example.library.entity.Publisher;
import com.example.library.repository.PublisherRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Transactional(readOnly = true)
    public List<PublisherResponse> getAllPublishers() {
        return publisherRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PublisherResponse getPublisherById(Long id) {
        return toResponse(findPublisher(id));
    }

    @Transactional
    public PublisherResponse createPublisher(PublisherRequest request) {
        if (publisherRepository.existsByNameIgnoreCase(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Publisher already exists");
        }

        Publisher publisher = new Publisher();
        applyRequest(publisher, request);

        return toResponse(publisherRepository.save(publisher));
    }

    @Transactional
    public PublisherResponse updatePublisher(
            Long id, PublisherRequest request) {

        Publisher publisher = findPublisher(id);

        publisherRepository.findByNameIgnoreCase(request.name())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Publisher already exists");
                    }
                });

        applyRequest(publisher, request);
        return toResponse(publisherRepository.save(publisher));
    }

    @Transactional
    public void deletePublisher(Long id) {
        Publisher publisher = findPublisher(id);

        if (!publisher.getBooks().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete publisher with books");
        }

        publisherRepository.delete(publisher);
    }

    @Transactional(readOnly = true)
    public List<PublisherResponse> searchByName(String name) {
        return publisherRepository
                .findByNameContainingIgnoreCase(name)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PublisherResponse> searchByCountry(String country) {
        return publisherRepository.findByCountryIgnoreCase(country)
                .stream().map(this::toResponse).toList();
    }

    private Publisher findPublisher(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Publisher not found"));
    }

    private void applyRequest(
            Publisher publisher, PublisherRequest request) {
        publisher.setName(request.name());
        publisher.setCountry(request.country());
    }

    private PublisherResponse toResponse(Publisher publisher) {
        return new PublisherResponse(
                publisher.getId(),
                publisher.getName(),
                publisher.getCountry()
        );
    }
}
