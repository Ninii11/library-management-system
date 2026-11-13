
package com.example.library.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PublisherRequest(
        @NotBlank String name,
        @NotBlank String country
) {}
