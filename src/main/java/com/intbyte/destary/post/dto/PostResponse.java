package com.intbyte.destary.post.dto;

import java.time.Instant;

public record PostResponse(
        Long id,
        Long userId,
        String content,
        Instant createdAt
) {}