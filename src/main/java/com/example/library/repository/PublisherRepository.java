
package com.example.library.repository;

import com.example.library.entity.Publisher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherRepository
        extends JpaRepository<Publisher, Long> {

    // Find publisher by exact name
    Optional<Publisher> findByNameIgnoreCase(String name);

    // Check whether publisher exists
    boolean existsByNameIgnoreCase(String name);

    // Search publishers by name
    List<Publisher> findByNameContainingIgnoreCase(String name);

    // Find publishers by country
    List<Publisher> findByCountryIgnoreCase(String country);

    // Find publishers that have books
    @Query("""
           SELECT DISTINCT p
           FROM Publisher p
           JOIN p.books b
           """)
    List<Publisher> findPublishersWithBooks();

    // Find publishers with more than N books
    @Query("""
           SELECT p
           FROM Publisher p
           JOIN p.books b
           GROUP BY p
           HAVING COUNT(b) > :count
           """)
    List<Publisher> findPublishersWithMoreThanBooks(
            @Param("count") long count
    );
}
