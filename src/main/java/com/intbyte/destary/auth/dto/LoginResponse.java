package com.intbyte.destary.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
