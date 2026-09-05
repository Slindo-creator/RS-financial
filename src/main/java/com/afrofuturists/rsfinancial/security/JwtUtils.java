package com.afrofuturists.rsfinancial.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    private final String jwtSecret;
    private final long jwtExpirationMs;

    public JwtUtils(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.expiration-ms}") long jwtExpirationMs
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7).trim();
            return token.isEmpty() ? null : token;
        }

        return null;
    }

    public String generateTokenFromUsername(StaffUserDetails userDetails) {
        // The role claim below is a convenience for clients (e.g. so a
        // frontend can render the right nav without a separate call) -
        // it is NOT what authorization decisions are based on. Every
        // request still re-loads the StaffUser's current role from the
        // database via StaffUserDetailsService, so revoking or changing
        // a role takes effect immediately rather than waiting for the
        // token to expire.
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", userDetails.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException e) {
            // Covers malformed, expired, unsupported, and bad-signature
            // tokens - JwtException is the common superclass for all of
            // JJWT's parsing/verification failures, so this catches
            // tampered signatures too (missed in the original, which only
            // listed three specific subclasses and IllegalArgumentException).
            log.debug("Rejected JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.debug("Rejected JWT - empty or malformed claims: {}", e.getMessage());
        }
        return false;
    }
}
