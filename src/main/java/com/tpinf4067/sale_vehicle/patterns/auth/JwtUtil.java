package com.tpinf4067.sale_vehicle.patterns.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    // ✅ Clé secrète statique et sécurisée (⚠ Change-la avant de passer en prod)
    private static final String SECRET = "MY_SUPER_SECRET_KEY_FOR_JWT_AUTHENTICATION_256_BITS"; 
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(Base64.getEncoder().encode(SECRET.getBytes()));

    // ✅ Génération du token avec un rôle bien défini
    public String generateToken(String username, Role role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role.name()) // 🔥 Correction : ne pas ajouter "ROLE_" ici, Spring le fait automatiquement
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Expiration 1h
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Extraction d'une valeur depuis le JWT
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    // ✅ Extraction du username depuis le token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Extraction du rôle depuis le token
    public String extractUserRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class)); 
    }

    // ✅ Extraction de la date d'expiration
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ✅ Validation du token (vérifie si le username correspond et si le token est expiré)
    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !extractExpiration(token).before(new Date());
    }
}
