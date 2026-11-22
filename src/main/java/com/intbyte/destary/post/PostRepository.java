package com.intbyte.destary.post;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class PostRepository {
    private final JdbcClient client;

    public PostRepository(JdbcClient client) {
        this.client = client;
    }

    public long getAmountOfUserPosts(long userId) {
        return client.sql("SELECT COUNT(*) FROM posts WHERE user_id = :user_id")
                .param("user_id", userId)
                .query(Long.class)
                .single();
    }

    public Optional<Post> findPostByIndex(long userId, long index) {
        return client.sql("SELECT id, user_id, content, created_at FROM posts WHERE user_id = :user_id ORDER BY created_at DESC, id DESC LIMIT 1 OFFSET :index")
                .param("user_id", userId)
                .param("index", index)
                .query(this::mapPost)
                .optional();
    }

    public Optional<Post> findPostById(long id) {
        return client.sql("SELECT id, user_id, content, created_at FROM posts WHERE id = :id LIMIT 1")
                .param("id", id)
                .query(this::mapPost)
                .optional();
    }

    private Post mapPost(ResultSet resultSet, int row) throws SQLException {
        return new Post(
                resultSet.getLong("id"),
                resultSet.getLong("user_id"),
                resultSet.getString("content"),
                resultSet.getObject("created_at", java.time.OffsetDateTime.class).toInstant()
        );
    }

    public Post createPost(Long userId, String content) {
        return client.sql("INSERT INTO posts (user_id, content) VALUES (:user_id, :content) RETURNING *")
                .param("user_id", userId)
                .param("content", content)
                .query(this::mapPost)
                .single();
    }
}
