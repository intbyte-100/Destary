package com.intbyte.destary.post;

import com.intbyte.destary.post.dto.CreatePostRequest;
import com.intbyte.destary.post.dto.PostResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PostResponse>  createPost(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreatePostRequest request
    ) {
        long userId = Long.parseLong(Objects.requireNonNull(jwt.getSubject()));

        var post = service.createPost(userId, request);

        return ResponseEntity
                .created(URI.create("/api/posts/" + post.postId()))
                .body(post.toPostResponse());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId
    ) {
        Post post = service.getById(postId);

        return ResponseEntity.ok(post.toPostResponse());
    }
}
