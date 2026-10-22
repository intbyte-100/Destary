package com.intbyte.destary.auth;

import com.intbyte.destary.auth.dto.RefreshResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

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
        Optional<RefreshToken> tokenOptional = repository.findByHash(TokenHasher.sha256(oldToken));

        System.out.println(tokenOptional);
        if (tokenOptional.map((it) -> !it.isActive()).orElse(true)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The refresh token was expired or revoked");
        }

        RefreshToken token = tokenOptional.get();
        repository.deleteById(token.id());

        return new RefreshResponse(token.userId(), createRefreshToken(token.userId()));
    }


}
