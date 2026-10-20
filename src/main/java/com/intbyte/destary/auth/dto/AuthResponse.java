package com.intbyte.destary.auth.dto;

public record AuthResponse(
        long id,
        String username,
        String email
) {}