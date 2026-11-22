package com.intbyte.destary.user;

import com.intbyte.destary.user.dto.PublicUserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class PublicUserController {
    private final UserService service;

    public PublicUserController(UserService service) { this.service = service; }

    @GetMapping("/{userId}")
    public PublicUserResponse getUser(@PathVariable long userId) {
        return service.publicProfile(userId);
    }
}
