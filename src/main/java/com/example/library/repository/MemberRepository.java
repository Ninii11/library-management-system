
package com.example.library.repository;

import com.example.library.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository
        extends JpaRepository<Member, Long> {

    // Find member by email
    Optional<Member> findByEmailIgnoreCase(String email);

    // Check if email already exists
    boolean existsByEmailIgnoreCase(String email);

    // Search members by first name
    List<Member> findByFirstNameContainingIgnoreCase(String name);

    // Search members by last name
    List<Member> findByLastNameContainingIgnoreCase(String name);

    // Find members who have borrowed books
    @Query("""
           SELECT DISTINCT m
           FROM Member m
           JOIN m.loans l
           """)
    List<Member> findMembersWithLoans();

    // Find members with active loans
    @Query("""
           SELECT DISTINCT m
           FROM Member m
           JOIN m.loans l
           WHERE l.status = 'BORROWED'
           """)
    List<Member> findMembersWithActiveLoans();

    // Find members with more than N loans
    @Query("""
           SELECT m
           FROM Member m
           JOIN m.loans l
           GROUP BY m
           HAVING COUNT(l) > :count
           """)
    List<Member> findMembersWithMoreThanLoans(
            @Param("count") long count
    );
}
