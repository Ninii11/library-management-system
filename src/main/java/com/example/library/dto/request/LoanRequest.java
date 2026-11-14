
package com.example.library.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LoanRequest(
        @NotNull @Positive Long memberId,
        @NotNull @Positive Long bookId,
        @Min(1) int days
) {}
