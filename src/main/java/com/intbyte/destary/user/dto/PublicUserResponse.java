package com.intbyte.destary.user.dto;

import java.time.Instant;

public record PublicUserResponse(long id, String username, Instant createdAt) {}
