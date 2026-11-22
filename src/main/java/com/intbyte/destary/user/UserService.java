package com.intbyte.destary.user;

import com.intbyte.destary.auth.dto.AuthResponse;
import com.intbyte.destary.auth.dto.LoginRequest;
import com.intbyte.destary.user.dto.MeResponse;
import com.intbyte.destary.user.dto.PublicUserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public MeResponse me(long userId) {
        User user = findUser(userId);

        return new MeResponse(user.username(), user.email());
    }

    public PublicUserResponse publicProfile(long userId) {
        User user = findUser(userId);
        return new PublicUserResponse(user.id(), user.username(), user.createdAt());
    }

    private User findUser(long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
