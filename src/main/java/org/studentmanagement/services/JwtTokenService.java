package org.studentmanagement.services;

import org.springframework.stereotype.Service;
import org.studentmanagement.data.entities.TokenEntity;
import org.studentmanagement.data.entities.UserEntity;

@Service
public interface JwtTokenService {
    TokenEntity generateToken(UserEntity user);
    Boolean validateToken(String token);
    String getEmailFromToken(String token);
    void invalidateToken(String token);
}
