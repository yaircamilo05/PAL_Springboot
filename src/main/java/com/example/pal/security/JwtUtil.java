package com.example.pal.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.pal.dto.UserResponseDTO;

import javax.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String base64Secret;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    private Key key;
    private JwtParser parser;

    @PostConstruct
    public void init() {
        byte[] keyBytes;
        // si contiene '_' o '-', asumimos Base64URL:
        if (base64Secret.contains("_") || base64Secret.contains("-")) {
            keyBytes = Decoders.BASE64URL.decode(base64Secret);
        } else {
            // si no, lo tratamos como UTF-8 plain text
            keyBytes = base64Secret.getBytes(StandardCharsets.UTF_8);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.parser = Jwts.parserBuilder()
                          .setSigningKey(key)
                          .build();
    }

    public String generateToken(UserResponseDTO username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                   .setSubject(username.getUsername())
                   .claim("id", username.getId())
                     .claim("roles", username.getRoles())
                   .setIssuedAt(now)
                   .setExpiration(exp)
                   .signWith(key, SignatureAlgorithm.HS256)
                   .compact();
    }

    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = getUsernameFromToken(token);
            return extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }


    public String getUsernameFromToken(String token) {
        return parser.parseClaimsJws(token)
                     .getBody()
                     .getSubject();
    }

    
    public boolean isTokenExpired(String token) {
        Date expiration = parser.parseClaimsJws(token).getBody().getExpiration();
        return expiration.before(new Date());
    }

}

