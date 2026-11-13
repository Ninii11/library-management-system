
package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Method-name query 1: Search books by title
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Method-name query 2: Find books cheaper than a price
    List<Book> findByPriceLessThan(double price);

    // Custom query 1: Find books in a price range
    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :min AND :max")
    List<Book> findBooksByPriceRange(
            @Param("min") double min,
            @Param("max") double max
    );

    // Custom query 2: Find books by category
    @Query("SELECT DISTINCT b FROM Book b JOIN b.categories c WHERE c.name = :name")
    List<Book> findBooksByCategory(
            @Param("name") String name
    );

    @org.springframework.data.jpa.repository.Lock(
            jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query(
            "SELECT b FROM Book b WHERE b.id = :id")
    java.util.Optional<com.example.library.entity.Book> findByIdForUpdate(
            @org.springframework.data.repository.query.Param("id") Long id);
}
