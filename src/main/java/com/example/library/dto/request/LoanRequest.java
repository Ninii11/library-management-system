
package com.example.library.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull Long memberId,
        @NotNull Long bookId,
        @Min(1) int days
) {}
