package com.intbyte.destary.user;

import java.time.Instant;

public record User(
        long id,
        String username,
        String email,
        String passwordHash,
        Instant createdAt
) {}