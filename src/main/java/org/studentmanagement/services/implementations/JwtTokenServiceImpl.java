package org.studentmanagement.services.implementations;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studentmanagement.data.entities.TokenEntity;
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.repositories.TokenRepository;
import org.studentmanagement.providers.JwtDataProvider;
import org.studentmanagement.services.JwtTokenService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    private final TokenRepository tokenRepository;
    private final JwtDataProvider jwtDataProvider;

    @Autowired
    public JwtTokenServiceImpl(TokenRepository tokenRepository,
                               JwtDataProvider jwtDataProvider) {
        this.tokenRepository = tokenRepository;
        this.jwtDataProvider = jwtDataProvider;
    }

    @Override
    public String generateToken(UserEntity user) {
        deleteExistingToken(user);

        Instant currentTime = Instant.now();
        Instant expirationTime = currentTime.plus(jwtDataProvider.getExpiration(), ChronoUnit.MINUTES);

        String token = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(Date.from(currentTime))
                .expiration(Date.from(expirationTime))
                .signWith(jwtDataProvider.getKey())
                .compact();

        TokenEntity tokenEntity = new TokenEntity(
                user,
                token,
                Date.from(currentTime),
                Date.from(expirationTime)
        );

        tokenRepository.save(tokenEntity);

        return token;
    }

    private void deleteExistingToken(UserEntity user) {
        Optional<TokenEntity> tokenEntity = tokenRepository.findTokenEntityByUser(user);
        tokenEntity.ifPresent(tokenRepository::delete);
    }

    @Override
    public Boolean validateToken(String token) {
        try {
            innerValidateToken(token);
        } catch (MalformedJwtException |
                 SecurityException |
                 ExpiredJwtException |
                 IllegalArgumentException |
                 SignatureException ex) {
            return false;
        }

        return true;
    }

    private void innerValidateToken(String token) {
        String email = getSubject(token);
        TokenEntity tokenEntity = tokenRepository.findTokenEntityByTokenAndUserEmail(token, email)
                .orElseThrow(IllegalArgumentException::new);

        Instant currentTime = Instant.now();

        if (tokenEntity.getExpirationDate().toInstant().isBefore(currentTime)) {
            tokenRepository.delete(tokenEntity);
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String getEmailFromToken(String token) {
        return getSubject(token);
    }

    @Override
    public void invalidateToken(String token) {
        String email = getSubject(token);
        tokenRepository.findTokenEntityByTokenAndUserEmail(token, email).ifPresent(tokenRepository::delete);
    }

    private String getSubject(String token) {
        return Jwts.parser()
                .verifyWith(jwtDataProvider.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
