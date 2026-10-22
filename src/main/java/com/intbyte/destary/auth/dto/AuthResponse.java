package com.intbyte.destary.auth.dto;

public record AuthResponse(
        long userId,
        String accessToken,
        String refreshToken,
        String tokenType
) {}