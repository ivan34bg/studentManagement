package org.studentmanagement.providers.implementations;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.studentmanagement.providers.JwtDataProvider;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

@Component
public class JwtDataProviderImpl implements JwtDataProvider {
    private final Environment environment;

    public JwtDataProviderImpl(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Long getExpiration() {
        return Long.valueOf(environment.getProperty("app.jwt.expiration"));
    }

    @Override
    public SecretKey getSecretKey() {
        String secret = environment.getProperty("app.jwt.key");
        String algorithm = environment.getProperty("app.jwt.key.algorithm");
        return new SecretKeySpec(Decoders.BASE64.decode(secret), algorithm);
    }

    @Override
    public Key getKey() {
        return Keys.hmacShaKeyFor(getSecretKey().getEncoded());
    }
}
