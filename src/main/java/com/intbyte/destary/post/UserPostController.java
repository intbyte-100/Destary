package com.intbyte.destary.post;

import com.intbyte.destary.post.dto.PostResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users/{userId}/posts")
public class UserPostController {

    private final PostService service;

    public UserPostController(PostService postService) {
        this.service = postService;
    }

    @GetMapping
    public PostResponse getUserPostByIndex(
            @PathVariable Long userId,
            @RequestParam long index
    ) {
        if (index < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Index must be non-negative");
        }
        return service.getByIndex(userId, index).toPostResponse();
    }

    @GetMapping("/count")
    public long getUserPostCount(@PathVariable Long userId) {
        return service.getUserPostAmount(userId);
    }
}
