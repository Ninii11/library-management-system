
package com.example.library.repository;

import com.example.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Find an author by exact name
    Optional<Author> findByNameIgnoreCase(String name);

    // Search authors by partial name
    List<Author> findByNameContainingIgnoreCase(String name);

    // Find authors who have written at least one book
    @Query("SELECT DISTINCT a FROM Author a JOIN a.books b")
    List<Author> findAuthorsWithBooks();

    // Find authors who have written more than N books
    @Query("SELECT a FROM Author a JOIN a.books b " +
            "GROUP BY a HAVING COUNT(b) > :count")
    List<Author> findAuthorsWithMoreThanBooks(
            @Param("count") long count
    );
}
