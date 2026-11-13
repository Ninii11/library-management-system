
package com.example.library.repository;

import com.example.library.entity.Loan;
import com.example.library.entity.LoanStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository
        extends JpaRepository<Loan, Long> {

    // Find all loans belonging to a member
    List<Loan> findByMemberId(Long memberId);

    // Find all loans for a book
    List<Loan> findByBookId(Long bookId);

    // Find loans by status
    List<Loan> findByStatus(LoanStatus status);

    // Find active loans for a member
    List<Loan> findByMemberIdAndStatus(
            Long memberId,
            LoanStatus status
    );

    // Check whether a book is currently borrowed
    boolean existsByBookIdAndStatus(
            Long bookId,
            LoanStatus status
    );

    // Find overdue loans
    @Query("""
           SELECT l
           FROM Loan l
           WHERE l.dueDate < :today
           AND l.status = :status
           """)
    List<Loan> findOverdueLoans(
            @Param("today") LocalDate today,
            @Param("status") LoanStatus status
    );

    // Find loans between two dates
    @Query("""
           SELECT l
           FROM Loan l
           WHERE l.borrowDate BETWEEN :start AND :end
           ORDER BY l.borrowDate DESC
           """)
    List<Loan> findLoansBetweenDates(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // Find members' borrowing history
    @Query("""
           SELECT l
           FROM Loan l
           WHERE l.member.id = :memberId
           ORDER BY l.borrowDate DESC
           """)
    List<Loan> findMemberLoanHistory(
            @Param("memberId") Long memberId
    );
}
