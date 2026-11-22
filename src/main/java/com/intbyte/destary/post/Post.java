package com.intbyte.destary.post;

import com.intbyte.destary.post.dto.PostResponse;

import java.time.Instant;

public record Post(long postId, long userId, String content, Instant createdAt) {
    public PostResponse toPostResponse() {
        return new PostResponse(postId(), userId(), content(), createdAt());
    }
}
