
package com.example.library.dto.request;

import jakarta.validation.constraints.*;

public record AuthorRequest(

        @NotBlank(message = "Author name is required")
        @Size(min = 2, max = 100)
        String name

) {}
