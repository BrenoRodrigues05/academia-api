package com.academia.academia_api.infra.security;

import com.academia.academia_api.entity.Usuarios;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(Usuarios usuario) {

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer("academia-api")
                .withSubject(usuario.getLogin())
                .withExpiresAt(generateExpirationDate())
                .sign(algorithm);
    }

    private Instant generateExpirationDate() {

        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}