package com.intbyte.destary.auth.dto;

public record LoginResponse(
        String accessToken,
        String tokenType
) {
}
