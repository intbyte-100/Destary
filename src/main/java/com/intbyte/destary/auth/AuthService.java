package com.intbyte.destary.auth;

import com.intbyte.destary.auth.dto.AuthResponse;
import com.intbyte.destary.auth.dto.LoginRequest;
import com.intbyte.destary.auth.dto.LoginResponse;
import com.intbyte.destary.auth.dto.RegisterRequest;
import com.intbyte.destary.user.User;
import com.intbyte.destary.user.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder encoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must not exceed 72 bytes"
            );
        }

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username is already taken"
            );
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        String passwordHash = encoder.encode(request.password());

        try {
            User user = userRepository.create(
                    request.username(),
                    request.email(),
                    passwordHash
            );

            return new AuthResponse(
                    user.id(),
                    user.username(),
                    user.email()
            );
        } catch (DuplicateKeyException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username or email is already registered"
            );
        }
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.username());

        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User with this username is not registered yet");
        }

        User user = userOptional.get();

        if (encoder.matches(request.password(), user.passwordHash())) {
            return new LoginResponse(jwtService.createAccessToken(user.id()), "Bearer");
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The password is incorrect");
    }
}
