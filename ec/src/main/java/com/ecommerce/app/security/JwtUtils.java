package com.ecommerce.app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.ecommerce.app.security.UserDetailsImpl; // Ensure this import is correct
import jakarta.annotation.PostConstruct; // Use jakarta.annotation.PostConstruct for Spring Boot 3+
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${jwt.expirationMs:86400000}")
    private int jwtExpirationMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            logger.warn("JWT Secret Key not found in application.properties. Generating a random secure key.");
            SecureRandom secureRandom = new SecureRandom();
            byte[] keyBytes = new byte[32]; // 32 bytes = 256 bits
            secureRandom.nextBytes(keyBytes);
            this.jwtSecret = Base64.getEncoder().encodeToString(keyBytes);
            logger.warn("Generated JWT Secret Key (for development/testing only): {}", this.jwtSecret);
        }
        try {
            // Ensure the secret is a valid Base64 string for decoding
            this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
            logger.info("JWT Signing Key initialized.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to decode JWT Secret Key. It might not be a valid Base64 string. Please check 'jwt.secret' in application.properties or ensure the generated key is correct.", e);
            // Optionally, you might want to re-generate or set a fallback key here if decoding fails
            SecureRandom secureRandom = new SecureRandom();
            byte[] keyBytes = new byte[32];
            secureRandom.nextBytes(keyBytes);
            this.jwtSecret = Base64.getEncoder().encodeToString(keyBytes);
            this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
            logger.warn("Regenerated JWT Secret Key due to decoding failure: {}", this.jwtSecret);
        }
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return signingKey;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}