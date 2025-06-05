package com.ecommerce.app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct; // For initialization method
import java.security.Key;
import java.security.SecureRandom; // For secure random key generation
import java.util.Base64; // For Base64 encoding
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Ye field application.properties se value lega agar provide ki gayi hai.
    // Agar nahi provide ki gayi ya empty hai, toh hum khud generate karenge.
    @Value("${jwt.secret:}") // Default empty string agar property define nahi ki
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    private Key signingKey; // Actual Key object jise hum generate ya decode karenge

    // PostConstruct annotation se ye method bean initialize hone ke baad run hoga
    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            // Agar secret key application.properties mein nahi di gayi, toh generate karo
            logger.warn("JWT Secret Key not found in application.properties. Generating a random secure key.");
            SecureRandom secureRandom = new SecureRandom();
            byte[] keyBytes = new byte[32]; // 256 bits (HS256)
            secureRandom.nextBytes(keyBytes);
            this.jwtSecret = Base64.getEncoder().encodeToString(keyBytes);
            logger.warn("Generated JWT Secret Key (for development/testing only): {}", this.jwtSecret);
            // WARNING: Production mein hamesha environment variable ya secret management service se key leni chahiye.
        }
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        logger.info("JWT Signing Key initialized.");
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256) // Ab hum pre-generated signingKey use karenge
                .compact();
    }

    // Ab direct signingKey return karenge jo init() mein set hua hai
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