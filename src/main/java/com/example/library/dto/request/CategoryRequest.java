
package com.example.library.dto.request;

import jakarta.validation.constraints.*;

public record CategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(min = 2, max = 100)
        String name

) {}
