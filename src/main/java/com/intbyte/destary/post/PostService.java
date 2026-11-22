package com.intbyte.destary.post;

import com.intbyte.destary.post.dto.CreatePostRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.function.Supplier;

@Service
public class PostService {
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public Post createPost(long userId, CreatePostRequest request) {
        return repository.createPost(userId, request.content());
    }

    private Post findBy(Supplier<Optional<Post>> function) {
        var post = function.get();

        if (post.isPresent()) {
            return post.get();
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    public Post getById(long postId) {
        return findBy(() -> this.repository.findPostById(postId));
    }

    public Post getByIndex(long userId, long index) {
        return findBy(() -> this.repository.findPostByIndex(userId, index));
    }

    public long getUserPostAmount(long userId) {
        return repository.getAmountOfUserPosts(userId);
    }
}
