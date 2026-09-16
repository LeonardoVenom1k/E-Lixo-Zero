package br.fai.lds.e_lixo_zero.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtTokenService {

    @Value("${jwt.secret:elixo-zero-secret-key-change-in-production-1234567890}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private SecretKey getSigningKey() {
        final byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(final String email) {
        final Date now = new Date();
        final Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isValidToken(final String token) {
        try {
            final Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (final Exception e) {
            return false;
        }
    }

    public String getSubjectFromToken(final String token) {
        return parseToken(token).getSubject();
    }

    private Claims parseToken(final String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
