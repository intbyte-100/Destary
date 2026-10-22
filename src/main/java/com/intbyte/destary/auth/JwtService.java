package com.intbyte.destary.auth;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {
    private final JwtEncoder encoder;

    public JwtService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String createAccessToken(long userId) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(Long.toString(userId))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60 * 15))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return encoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}
