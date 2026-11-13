
package com.example.library.dto.response;

import com.example.library.entity.LoanStatus;
import java.time.LocalDate;

public record LoanResponse(
        Long id,
        Long memberId,
        String memberName,
        Long bookId,
        String bookTitle,
        LocalDate borrowDate,
        LocalDate dueDate,
        LocalDate returnDate,
        LoanStatus status
) {}
