package com.intbyte.destary.auth;

import java.time.Instant;

public record RefreshToken(long id, String token, long userId, Instant expiresAt, Instant revokedAt, Instant createdAt) {
    public boolean isActive() {
        return expiresAt().isAfter(Instant.now()) && revokedAt == null;
    }
}
