
package com.example.library.dto.response;

import java.util.Set;

public record BookResponse(
        Long id,
        String title,
        double price,
        String coverImagePath,
        Long authorId,
        String authorName,
        Set<String> categories,
        Long publisherId,
        String publisherName
) {}
