
package com.example.library.repository;

import com.example.library.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find category by name
    Optional<Category> findByNameIgnoreCase(String name);

    // Search categories by partial name
    List<Category> findByNameContainingIgnoreCase(String name);

    // Find categories that contain books
    @Query("SELECT DISTINCT c FROM Category c JOIN c.books b")
    List<Category> findCategoriesWithBooks();

    // Find categories containing more than N books
    @Query("SELECT c FROM Category c JOIN c.books b " +
            "GROUP BY c HAVING COUNT(b) > :count")
    List<Category> findCategoriesWithMoreThanBooks(
            @Param("count") long count
    );
}
