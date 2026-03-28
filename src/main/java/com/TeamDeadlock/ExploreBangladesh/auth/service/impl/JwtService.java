package com.TeamDeadlock.ExploreBangladesh.auth.service.impl;

import com.TeamDeadlock.ExploreBangladesh.auth.entity.Role;
import com.TeamDeadlock.ExploreBangladesh.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    public long getAccessTtlSeconds() {
        return accessTtlSeconds;
    }

    public long getRefreshTtlSeconds() {
        return refreshTtlSeconds;
    }

    public String getIssuer() {
        return issuer;
    }

    public JwtService(
            @Value("${security.jwt.secret:${jwt.secret}}") String secret,
            @Value("${security.jwt.access-ttl-seconds:86400}") long accessTtlSeconds,
            @Value("${security.jwt.refresh-ttl-seconds:604800}") long refreshTtlSeconds,
            @Value("${security.jwt.issuer:ExploreBangladesh}") String issuer) {

        this.signingKey = buildSigningKey(secret);
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        List<String> roles = user.getRoles() == null
                ? List.of()
                : user.getRoles().stream().map(Role::getName).toList();

        String subject = user.getId() != null ? user.getId().toString() : user.getUsername();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(subject)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "typ", "access"
                ))
                .signWith(signingKey, Jwts.SIG.HS512)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return generateRefreshToken(user, UUID.randomUUID().toString());
    }

    public String generateRefreshToken(User user, String jti) {
        Instant now = Instant.now();
        String subject = user.getId() != null ? user.getId().toString() : user.getUsername();

        return Jwts.builder()
                .id(jti)
                .subject(subject)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claim("typ", "refresh")
                .signWith(signingKey, Jwts.SIG.HS512)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);
    }

    public boolean isAccessToken(String token) {
        Claims claims = parse(token).getPayload();
        return "access".equals(claims.get("typ", String.class));
    }

    public boolean isRefreshToken(String token) {
        Claims claims = parse(token).getPayload();
        return "refresh".equals(claims.get("typ", String.class));
    }

    public UUID getUserId(String token) {
        Claims claims = parse(token).getPayload();
        return UUID.fromString(claims.getSubject());
    }

    public String getJti(String token) {
        return parse(token).getPayload().getId();
    }

    public List<String> getRoles(String token) {
        Claims claims = parse(token).getPayload();
        Object rawRoles = claims.get("roles");
        if (!(rawRoles instanceof List<?> list)) {
            return List.of();
        }
        return list.stream().map(String::valueOf).toList();
    }

    public String getEmail(String token) {
        Claims claims = parse(token).getPayload();
        return claims.get("email", String.class);
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        String email = claims.get("email", String.class);
        return email != null ? email : claims.getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Backward compatible method used by existing login flow.
    public String generateToken(UserDetails userDetails) {
        if (userDetails instanceof User user) {
            return generateAccessToken(user);
        }

        Instant now = Instant.now();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userDetails.getUsername())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claim("typ", "access")
                .signWith(signingKey, Jwts.SIG.HS512)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (SignatureException | IllegalArgumentException ex) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return parse(token).getPayload();
    }

    private SecretKey buildSigningKey(String rawSecret) {
        if (rawSecret == null || rawSecret.isBlank()) {
            throw new IllegalArgumentException("JWT secret cannot be null or blank.");
        }

        byte[] keyBytes = null;

        try {
            byte[] decoded = Decoders.BASE64.decode(rawSecret);
            if (decoded.length >= 64) {
                keyBytes = decoded;
            }
        } catch (IllegalArgumentException ex) {
            // Not base64, fallback to raw bytes.
        }

        if (keyBytes == null) {
            keyBytes = rawSecret.getBytes(StandardCharsets.UTF_8);
        }

        if (keyBytes.length < 64) {
            // Derive a stable 64-byte key for HS512 when the configured secret is shorter.
            keyBytes = deriveHs512Key(rawSecret);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] deriveHs512Key(String rawSecret) {
        try {
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
            return sha512.digest(rawSecret.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to initialize SHA-512 for JWT key derivation", e);
        }
    }
}
