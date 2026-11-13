
package com.example.library.dto.response;

public record MemberResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone
) {}
