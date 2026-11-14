
package com.example.library.dto.request;

import jakarta.validation.constraints.*;
import java.util.Set;

public record BookRequest(
        @NotBlank String title,
        @NotNull @Positive Double price,
        @NotNull @Positive Long authorId,
        @NotEmpty Set<@NotNull @Positive Long> categoryIds,
        @Positive Long publisherId
) {}
