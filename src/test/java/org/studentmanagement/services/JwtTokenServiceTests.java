package org.studentmanagement.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.studentmanagement.data.entities.TokenEntity;
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.repositories.TokenRepository;
import org.studentmanagement.providers.DateProvider;
import org.studentmanagement.providers.JwtDataProvider;
import org.studentmanagement.providers.TimeProvider;
import org.studentmanagement.services.implementations.JwtTokenServiceImpl;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@SpringBootTest
class JwtTokenServiceTests {
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private JwtDataProvider jwtDataProvider;
    @Mock
    private JwtBuilder jwtBuilder;
    @Mock
    private JwtParserBuilder jwtParserBuilder;
    @Mock
    private TimeProvider timeProvider;
    @Mock
    private DateProvider dateProvider;
    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setupBeforeTest() {
        tokenRepository = Mockito.mock(TokenRepository.class);
        jwtDataProvider = Mockito.mock(JwtDataProvider.class);
        jwtBuilder = Mockito.mock(JwtBuilder.class);
        timeProvider = Mockito.mock(TimeProvider.class);
        dateProvider = Mockito.mock(DateProvider.class);
        jwtTokenService = new JwtTokenServiceImpl(
                tokenRepository,
                jwtDataProvider,
                jwtBuilder,
                jwtParserBuilder,
                timeProvider,
                dateProvider
        );
    }

    @Test
    void testGenerateToken() {
        UserEntity expectedUser = Mockito.mock(UserEntity.class);
        TokenEntity expectedExistingToken = Mockito.mock(TokenEntity.class);
        long expectedExpirationTime = 100L;
        Key expectedKey = Mockito.mock(Key.class);
        String expectedEmail = "Test";
        Instant currentTime = Instant.now();
        Instant expirationTime = currentTime.plus(expectedExpirationTime, ChronoUnit.MINUTES);
        Date issueDate = Mockito.mock(Date.class);
        Date expirationDate = Mockito.mock(Date.class);

        Mockito.when(tokenRepository.findTokenEntityByUser(expectedUser))
                .thenReturn(Optional.of(expectedExistingToken));
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(currentTime);
        Mockito.when(dateProvider.getDateFrom(currentTime)).thenReturn(issueDate);
        Mockito.when(dateProvider.getDateFrom(expirationTime)).thenReturn(expirationDate);
        Mockito.when(jwtDataProvider.getExpiration())
                .thenReturn(expectedExpirationTime);
        Mockito.when(jwtDataProvider.getKey()).thenReturn(expectedKey);
        Mockito.when(jwtBuilder.subject(expectedEmail)).thenReturn(jwtBuilder);
        Mockito.when(jwtBuilder.issuedAt(issueDate)).thenReturn(jwtBuilder);
        Mockito.when(jwtBuilder.expiration(expirationDate)).thenReturn(jwtBuilder);
        Mockito.when(jwtBuilder.signWith(expectedKey)).thenReturn(jwtBuilder);
        Mockito.when(jwtBuilder.compact()).thenReturn("123");
        Mockito.when(expectedUser.getEmail()).thenReturn(expectedEmail);

        TokenEntity result = jwtTokenService.generateToken(expectedUser);

        Mockito.verify(timeProvider, Mockito.times(1)).getCurrentTime();
        Mockito.verify(dateProvider, Mockito.times(1)).getDateFrom(currentTime);
        Mockito.verify(dateProvider, Mockito.times(1)).getDateFrom(expirationTime);
        Mockito.verify(tokenRepository, Mockito.times(1)).findTokenEntityByUser(expectedUser);
        Mockito.verify(tokenRepository, Mockito.times(1)).delete(expectedExistingToken);
        Mockito.verify(jwtDataProvider, Mockito.times(1)).getExpiration();
        Mockito.verify(expectedUser, Mockito.times(1)).getEmail();
        Mockito.verify(jwtBuilder, Mockito.times(1)).subject(expectedEmail);
        Mockito.verify(jwtBuilder, Mockito.times(1)).issuedAt(issueDate);
        Mockito.verify(jwtBuilder, Mockito.times(1)).expiration(expirationDate);
        Mockito.verify(jwtBuilder, Mockito.times(1)).signWith(expectedKey);
        Mockito.verify(jwtBuilder, Mockito.times(1)).compact();
        Mockito.verify(jwtDataProvider, Mockito.times(1)).getKey();
        Mockito.verify(tokenRepository, Mockito.times(1)).save(result);
    }

    @Test
    void testValidateTokenWithValidUnexpiredToken() {
        SecretKey key = Mockito.mock(SecretKey.class);
        String token = "Token";
        JwtParser jwtParser = Mockito.mock(JwtParser.class);
        Jws<Claims> claims = Mockito.mock(Jws.class);
        Claims claimsObject = Mockito.mock(Claims.class);
        String expectedSubject = "Test";
        TokenEntity existingToken = Mockito.mock(TokenEntity.class);
        Instant currentTime = Instant.now();

        Mockito.when(jwtDataProvider.getSecretKey()).thenReturn(key);
        Mockito.when(jwtParserBuilder.verifyWith(key)).thenReturn(jwtParserBuilder);
        Mockito.when(jwtParserBuilder.build()).thenReturn(jwtParser);
        Mockito.when(jwtParser.parseSignedClaims(token)).thenReturn(claims);
        Mockito.when(claims.getPayload()).thenReturn(claimsObject);
        Mockito.when(claimsObject.getSubject()).thenReturn(expectedSubject);

        Mockito.when(tokenRepository.findTokenEntityByTokenAndUserEmail(token, expectedSubject))
                .thenReturn(Optional.of(existingToken));
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(currentTime);

        Date expirationDate = Mockito.mock(Date.class);
        Mockito.when(existingToken.getExpirationDate())
                .thenReturn(expirationDate);
        Instant expirationInstantTime = Mockito.mock(Instant.class);
        Mockito.when(expirationDate.toInstant()).thenReturn(expirationInstantTime);
        Mockito.when(expirationInstantTime.isBefore(currentTime)).thenReturn(false);

        Boolean result = jwtTokenService.validateToken(token);

        Mockito.verify(jwtDataProvider, Mockito.times(1)).getSecretKey();
        Mockito.verify(jwtParserBuilder, Mockito.times(1)).verifyWith(key);
        Mockito.verify(jwtParserBuilder, Mockito.times(1)).build();
        Mockito.verify(jwtParser, Mockito.times(1)).parseSignedClaims(token);
        Mockito.verify(claims, Mockito.times(1)).getPayload();
        Mockito.verify(claimsObject, Mockito.times(1)).getSubject();

        Mockito.verify(tokenRepository, Mockito.times(1))
                .findTokenEntityByTokenAndUserEmail(token, expectedSubject);
        Mockito.verify(timeProvider, Mockito.times(1)).getCurrentTime();
        Mockito.verify(existingToken, Mockito.times(1)).getExpirationDate();
        Mockito.verify(expirationDate, Mockito.times(1)).toInstant();
        Mockito.verify(expirationInstantTime, Mockito.times(1)).isBefore(currentTime);
        Mockito.verify(tokenRepository, Mockito.times(0)).delete(existingToken);

        Assertions.assertTrue(result);
    }

    @Test
    void testValidateTokenWithValidExpiredToken() {
        SecretKey key = Mockito.mock(SecretKey.class);
        String token = "Token";
        JwtParser jwtParser = Mockito.mock(JwtParser.class);
        Jws<Claims> claims = Mockito.mock(Jws.class);
        Claims claimsObject = Mockito.mock(Claims.class);
        String expectedSubject = "Test";
        TokenEntity existingToken = Mockito.mock(TokenEntity.class);
        Instant currentTime = Instant.now();

        Mockito.when(jwtDataProvider.getSecretKey()).thenReturn(key);
        Mockito.when(jwtParserBuilder.verifyWith(key)).thenReturn(jwtParserBuilder);
        Mockito.when(jwtParserBuilder.build()).thenReturn(jwtParser);
        Mockito.when(jwtParser.parseSignedClaims(token)).thenReturn(claims);
        Mockito.when(claims.getPayload()).thenReturn(claimsObject);
        Mockito.when(claimsObject.getSubject()).thenReturn(expectedSubject);

        Mockito.when(tokenRepository.findTokenEntityByTokenAndUserEmail(token, expectedSubject))
                .thenReturn(Optional.of(existingToken));
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(currentTime);
        Mockito.when(tokenRepository.findTokenEntityByToken(token)).thenReturn(Optional.of(existingToken));

        Date expirationDate = Mockito.mock(Date.class);
        Mockito.when(existingToken.getExpirationDate())
                .thenReturn(expirationDate);
        Instant expirationInstantTime = Mockito.mock(Instant.class);
        Mockito.when(expirationDate.toInstant()).thenReturn(expirationInstantTime);
        Mockito.when(expirationInstantTime.isBefore(currentTime)).thenReturn(true);

        Boolean result = jwtTokenService.validateToken(token);

        Mockito.verify(jwtDataProvider, Mockito.times(1)).getSecretKey();
        Mockito.verify(jwtParserBuilder, Mockito.times(1)).verifyWith(key);
        Mockito.verify(jwtParserBuilder, Mockito.times(1)).build();
        Mockito.verify(jwtParser, Mockito.times(1)).parseSignedClaims(token);
        Mockito.verify(claims, Mockito.times(1)).getPayload();
        Mockito.verify(claimsObject, Mockito.times(1)).getSubject();

        Mockito.verify(tokenRepository, Mockito.times(1))
                .findTokenEntityByTokenAndUserEmail(token, expectedSubject);
        Mockito.verify(timeProvider, Mockito.times(1)).getCurrentTime();
        Mockito.verify(existingToken, Mockito.times(1)).getExpirationDate();
        Mockito.verify(expirationDate, Mockito.times(1)).toInstant();
        Mockito.verify(expirationInstantTime, Mockito.times(1)).isBefore(currentTime);
        Mockito.verify(tokenRepository, Mockito.times(1)).delete(existingToken);
        Mockito.verify(tokenRepository, Mockito.times(1)).findTokenEntityByToken(token);

        Assertions.assertFalse(result);
    }

    @Test
    void testValidateTokenWithInvalidToken() {
        SecretKey key = Mockito.mock(SecretKey.class);
        String token = "Token";
        JwtParser jwtParser = Mockito.mock(JwtParser.class);
        Jws<Claims> claims = Mockito.mock(Jws.class);
        Claims claimsObject = Mockito.mock(Claims.class);
        String expectedSubject = "Test";

        Mockito.when(jwtDataProvider.getSecretKey()).thenReturn(key);
        Mockito.when(jwtParserBuilder.verifyWith(key)).thenReturn(jwtParserBuilder);
        Mockito.when(jwtParserBuilder.build()).thenReturn(jwtParser);
        Mockito.when(jwtParser.parseSignedClaims(token)).thenReturn(claims);
        Mockito.when(claims.getPayload()).thenReturn(claimsObject);
        Mockito.when(claimsObject.getSubject()).thenReturn(expectedSubject);
        Mockito.when(tokenRepository.findTokenEntityByToken(token)).thenReturn(Optional.empty());

        Mockito.when(tokenRepository.findTokenEntityByTokenAndUserEmail(token, expectedSubject)).thenReturn(null);

        Boolean result = jwtTokenService.validateToken(token);

        Mockito.verify(jwtDataProvider, Mockito.times(1)).getSecretKey();
        Mockito.verify(jwtParserBuilder, Mockito.times(1)).verifyWith(key);
        Mockito.verify(jwtParserBuilder, Mockito.times(1)).build();
        Mockito.verify(jwtParser, Mockito.times(1)).parseSignedClaims(token);
        Mockito.verify(claims, Mockito.times(1)).getPayload();
        Mockito.verify(claimsObject, Mockito.times(1)).getSubject();

        Mockito.verify(tokenRepository, Mockito.times(1))
                .findTokenEntityByTokenAndUserEmail(token, expectedSubject);
        Mockito.verify(tokenRepository, Mockito.times(1))
                .findTokenEntityByToken(token);

        Assertions.assertFalse(result);
    }

    @Test
    void testGetEmailFromToken() {
        String token = "Token";
        SecretKey key = Mockito.mock(SecretKey.class);
        JwtParser jwtParser = Mockito.mock(JwtParser.class);
        Jws<Claims> claims = Mockito.mock(Jws.class);
        Claims claimsObject = Mockito.mock(Claims.class);
        String expectedSubject = "Test";

        Mockito.when(jwtDataProvider.getSecretKey()).thenReturn(key);
        Mockito.when(jwtParserBuilder.verifyWith(key)).thenReturn(jwtParserBuilder);
        Mockito.when(jwtParserBuilder.build()).thenReturn(jwtParser);
        Mockito.when(jwtParser.parseSignedClaims(token)).thenReturn(claims);
        Mockito.when(claims.getPayload()).thenReturn(claimsObject);
        Mockito.when(claimsObject.getSubject()).thenReturn(expectedSubject);

        String result = jwtTokenService.getEmailFromToken(token);

        Mockito.verify(jwtDataProvider, Mockito.times(1)).getSecretKey();
        Mockito.verify(jwtParserBuilder, Mockito.times(1)).verifyWith(key);
        Mockito.verify(jwtParserBuilder, Mockito.times(1)).build();
        Mockito.verify(jwtParser, Mockito.times(1)).parseSignedClaims(token);
        Mockito.verify(claims, Mockito.times(1)).getPayload();
        Mockito.verify(claimsObject, Mockito.times(1)).getSubject();

        Assertions.assertEquals(expectedSubject, result);
    }
}
