package haru.pharmacy.service;

import haru.pharmacy.config.JwtProperties;
import haru.pharmacy.model.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service for working with JSON Web Tokens (JWT).
 * <p>
 * Responsible for generating, signing, and extracting data (Claims) from tokens.
 * Uses symmetric encryption (HMAC).
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Generates a cryptographic key based on the secret from the configuration.
     * <p>
     * <strong>Security Requirement:</strong> The configured secret MUST be at least 32 bytes (256 bits)
     * long to satisfy the HMAC-SHA algorithm security standards (RFC 7518).
     * <br>
     * Ensure that the `JWT_SECRET` environment variable is set correctly in your run configuration.
     * </p>
     *
     * @return The key object for HMAC-SHA signing.
     * @throws io.jsonwebtoken.security.WeakKeyException if the configured key is too short.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a new JWT token for a user.
     *
     * @param user The user entity for whom the token is being created.
     * @return The string representation of the token.
     */
    public String generateToken(UserAccount user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts all data (Claims) from the token.
     *
     * @param token The JWT token.
     * @return The Claims object.
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Utility method to extract the username from the token.
     *
     * @param token The JWT token.
     * @return Username (subject).
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
}
