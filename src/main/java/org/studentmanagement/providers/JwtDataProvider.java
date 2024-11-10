package org.studentmanagement.providers;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;

@Component
public interface JwtDataProvider {
    Long getExpiration();
    SecretKey getSecretKey();
    Key getKey();
}
