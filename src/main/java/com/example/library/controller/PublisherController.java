
package com.example.library.controller;

import com.example.library.dto.request.PublisherRequest;
import com.example.library.dto.response.PublisherResponse;
import com.example.library.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping
    public List<PublisherResponse> getAllPublishers() {
        return publisherService.getAllPublishers();
    }

    @GetMapping("/{id}")
    public PublisherResponse getPublisher(@PathVariable Long id) {
        return publisherService.getPublisherById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PublisherResponse createPublisher(
            @Valid @RequestBody PublisherRequest request) {
        return publisherService.createPublisher(request);
    }

    @PutMapping("/{id}")
    public PublisherResponse updatePublisher(
            @PathVariable Long id,
            @Valid @RequestBody PublisherRequest request) {
        return publisherService.updatePublisher(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
    }

    @GetMapping("/search")
    public List<PublisherResponse> searchPublishers(
            @RequestParam String name) {
        return publisherService.searchByName(name);
    }

    @GetMapping("/country")
    public List<PublisherResponse> getByCountry(
            @RequestParam String country) {
        return publisherService.searchByCountry(country);
    }
}
