package com.intbyte.destary.auth;

import com.intbyte.destary.auth.dto.RefreshResponse;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    public String createRefreshToken(long userId) {
        String token = RefreshTokenGenerator.generate();
        repository.create(userId, TokenHasher.sha256(token));

        return token;
    }

    public RefreshResponse updateRefreshToken(String oldToken) {
        Optional<RefreshToken> tokenOptional = repository.findByHash(
            TokenHasher.sha256(oldToken)
        );

        if (
            tokenOptional.map(it -> !it.isActive()).orElse(true) ||
            !repository.deleteById(tokenOptional.get().id())
        ) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "The refresh token was expired or revoked"
            );
        }

        RefreshToken token = tokenOptional.get();

        return new RefreshResponse(
            token.userId(),
            createRefreshToken(token.userId())
        );
    }
}
