package com.intbyte.destary.user;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcClient jdbc;

    public UserRepository(JdbcClient client) {
        this.jdbc = client;
    }

    public Optional<User> findByEmail(String email) {
        return jdbc.sql("""
                SELECT id,
                       username,
                       email,
                       password_hash,
                       created_at
                FROM users
                WHERE email = :email
                """).param("email", email).query(this::mapUser).optional();
    }

    public Optional<User> findByUsername(String username) {
        return jdbc.sql("""
                SELECT id,
                       username,
                       email,
                       password_hash,
                       created_at
                FROM users
                WHERE username = :username
                """).param("username", username).query(this::mapUser).optional();
    }

    public User create(String username, String email, String passwordHash) {
        return jdbc.sql("""
                INSERT INTO users (username, email, password_hash)
                VALUES (:username, :email, :passwordHash)
                RETURNING id, username, email, password_hash, created_at
                """).param("username", username).param("email", email).param("passwordHash", passwordHash).query(this::mapUser).single();
    }

    private User mapUser(ResultSet set, int rowNum) throws SQLException {
        return new User(set.getLong("id"), set.getString("username"), set.getString("email"), set.getString("password_hash"), set.getObject("created_at", java.time.OffsetDateTime.class).toInstant());
    }
}
