package com.intbyte.destary.auth;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Repository
public class RefreshTokenRepository {
    private final JdbcClient jdbc;

    public RefreshTokenRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<RefreshToken> findByHash(String token) {
        return jdbc.sql("SELECT * FROM refresh_tokens where token_hash = :token").param("token", token).query(this::mapToken).optional();
    }

    public RefreshToken create(long userId, String hash) {
        return jdbc.sql("""
                        INSERT INTO refresh_tokens (token_hash, user_id, expires_at)
                        VALUES (:token_hash, :user_id, :expires_at)
                        RETURNING id, token_hash, user_id, expires_at, revoked_at, created_at
                        """)
                .param("user_id", userId)
                .param("expires_at", Instant.now().plus(15, ChronoUnit.DAYS).atOffset(ZoneOffset.UTC))
                .param("token_hash", hash)
                .query(this::mapToken)
                .single();
    }

    public void deleteById(long id) {
        jdbc.sql("DELETE FROM refresh_tokens WHERE id = :id").param("id", id).query();
    }

    private RefreshToken mapToken(ResultSet set, int rowNum) throws SQLException {
        OffsetDateTime revokedAt = set.getObject("revoked_at", OffsetDateTime.class);

        return new RefreshToken(
                set.getLong("id"),
                set.getString("token_hash"),
                set.getLong("user_id"),
                set.getObject("expires_at", OffsetDateTime.class).toInstant(),
                revokedAt == null ? null : revokedAt.toInstant(),
                set.getObject("created_at", OffsetDateTime.class).toInstant());
    }
}
