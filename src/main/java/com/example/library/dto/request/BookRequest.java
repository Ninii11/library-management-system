
package com.example.library.dto.request;

import jakarta.validation.constraints.*;
import java.util.Set;

public record BookRequest(
        @NotBlank String title,
        @NotNull @Positive Double price,
        @NotNull Long authorId,
        @NotEmpty Set<Long> categoryIds,
        Long publisherId
) {}
