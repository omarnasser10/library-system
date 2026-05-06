package com.library.service;

import com.library.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final String SECRET_KEY =
            "mySuperSecretKeymySuperSecretKeymySuperSecretKey";

    // =========================
    // Generate Token
    // =========================
    public String generateToken(User user) {

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60)
                )
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // =========================
    // Extract Role
    // =========================
    public String extractRole(String token) {
        return extractAllClaims(token)
                .get("role", String.class);
    }

    // =========================
    // Extract User Id
    // =========================
    public Long extractId(String token) {
        return extractAllClaims(token)
                .get("id", Long.class);
    }

    // =========================
    // Validate Token
    // =========================
    public boolean isTokenValid(String token) {

        return !isTokenExpired(token);
    }

    // =========================
    // Check Expiration
    // =========================
    private boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // =========================
    // Parse Token
    // =========================
    private Claims extractAllClaims(String token) {

        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // =========================
    // Secret Key
    // =========================
    private Key getSignInKey() {

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}