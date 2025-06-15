package org.studentmanagement.services.implementations;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studentmanagement.data.entities.TokenEntity;
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.repositories.TokenRepository;
import org.studentmanagement.providers.DateProvider;
import org.studentmanagement.providers.JwtDataProvider;
import org.studentmanagement.providers.TimeProvider;
import org.studentmanagement.services.JwtTokenService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    private final TokenRepository tokenRepository;
    private final JwtDataProvider jwtDataProvider;
    private final JwtBuilder jwtBuilder;
    private final JwtParserBuilder jwtParserBuilder;
    private final TimeProvider timeProvider;
    private final DateProvider dateProvider;

    @Autowired
    public JwtTokenServiceImpl(TokenRepository tokenRepository,
                               JwtDataProvider jwtDataProvider,
                               TimeProvider timeProvider,
                               DateProvider dateProvider) {
        this.tokenRepository = tokenRepository;
        this.jwtDataProvider = jwtDataProvider;
        this.timeProvider = timeProvider;
        this.dateProvider = dateProvider;
        this.jwtBuilder = Jwts.builder();
        this.jwtParserBuilder = Jwts.parser();
    }

    public JwtTokenServiceImpl(TokenRepository tokenRepository,
                               JwtDataProvider jwtDataProvider,
                               JwtBuilder jwtBuilder,
                               JwtParserBuilder jwtParserBuilder,
                               TimeProvider timeProvider,
                               DateProvider dateProvider) {
        this.tokenRepository = tokenRepository;
        this.jwtDataProvider = jwtDataProvider;
        this.jwtBuilder = jwtBuilder;
        this.jwtParserBuilder = jwtParserBuilder;
        this.timeProvider = timeProvider;
        this.dateProvider = dateProvider;
    }

    @Override
    public TokenEntity generateToken(UserEntity user) {
        deleteExistingToken(user);

        Instant currentTime = timeProvider.getCurrentTime();
        Instant expirationTime = currentTime.plus(jwtDataProvider.getExpiration(), ChronoUnit.MINUTES);
        Date issueDate = dateProvider.getDateFrom(currentTime);
        Date expirationDate = dateProvider.getDateFrom(expirationTime);

        String token = jwtBuilder
                .subject(user.getEmail())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .signWith(jwtDataProvider.getKey())
                .compact();

        TokenEntity tokenEntity = new TokenEntity(
                user,
                token,
                issueDate,
                expirationDate
        );

        tokenRepository.save(tokenEntity);

        return tokenEntity;
    }

    private void deleteExistingToken(UserEntity user) {
        Optional<TokenEntity> tokenEntity = tokenRepository.findTokenEntityByUser(user);
        tokenEntity.ifPresent(tokenRepository::delete);
    }

    @Override
    public Boolean validateToken(String token) {
        try {
            innerValidateToken(token);
        } catch (RuntimeException e) {
            invalidateToken(token);
            return false;
        }

        return true;
    }

    private void innerValidateToken(String token) {
        String email = getSubject(token);
        TokenEntity tokenEntity = tokenRepository
                .findTokenEntityByTokenAndUserEmail(token, email)
                .orElseThrow(IllegalArgumentException::new);

        Instant currentTime = timeProvider.getCurrentTime();

        if (tokenEntity.getExpirationDate().toInstant().isBefore(currentTime)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String getEmailFromToken(String token) {
        return getSubject(token);
    }

    @Override
    public void invalidateToken(String token) {
        tokenRepository.findTokenEntityByToken(token)
                .ifPresent(tokenRepository::delete);
    }

    private String getSubject(String token) {
        return jwtParserBuilder
                .verifyWith(jwtDataProvider.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
