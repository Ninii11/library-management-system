
package com.example.library.service;

import com.example.library.dto.request.LoanRequest;
import com.example.library.dto.response.LoanResponse;
import com.example.library.entity.*;
import com.example.library.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    public LoanService(
            LoanRepository loanRepository,
            MemberRepository memberRepository,
            BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LoanResponse getLoanById(Long id) {
        return toResponse(findLoan(id));
    }

    @Transactional
    public LoanResponse borrowBook(LoanRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Member not found"));

        Book book = bookRepository.findByIdForUpdate(request.bookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found"));

        if (request.days() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Loan duration must be positive");
        }

        if (loanRepository.existsByBookIdAndStatus(
                book.getId(), LoanStatus.BORROWED)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Book is already borrowed");
        }

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(
                member, book, today, today.plusDays(request.days()));

        return toResponse(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse returnBook(Long loanId) {
        Loan loan = findLoan(loanId);

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Book already returned");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        return toResponse(loanRepository.save(loan));
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getActiveLoans() {
        return loanRepository.findByStatus(LoanStatus.BORROWED)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getOverdueLoans() {
        return loanRepository.findOverdueLoans(
                        LocalDate.now(), LoanStatus.BORROWED)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getMemberHistory(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Member not found");
        }

        return loanRepository.findMemberLoanHistory(memberId)
                .stream().map(this::toResponse).toList();
    }

    private Loan findLoan(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Loan not found"));
    }

    private LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getMember().getId(),
                loan.getMember().getFirstName() + " "
                        + loan.getMember().getLastName(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getBorrowDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus()
        );
    }
}
