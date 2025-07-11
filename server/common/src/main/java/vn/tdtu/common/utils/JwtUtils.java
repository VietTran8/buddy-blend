package vn.tdtu.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${security.secret}")
    private String jwtSecret;
    @Value("${security.expiration}")
    private long jwtExpirationMs;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserIdFromJwtToken(String bearerToken) {
        return getTokenSubject(bearerToken);
    }

    public String getTokenSubject(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith(Constants.BEARER_PREFIX))
            throw new IllegalArgumentException("Invalid JWT token format");

        String token = bearerToken.replace(Constants.BEARER_PREFIX, "");

        if (isTokenInvalid(token))
            throw new JwtException("JWT token is invalid");

        try {
            return Jwts.parserBuilder().setSigningKey(key()).build()
                    .parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    public boolean isTokenInvalid(String authToken) {
        if (authToken != null) {
            try {
                Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
                return false;
            } catch (MalformedJwtException e) {
                logger.error("Invalid JWT token: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                logger.error("JWT token is expired: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                logger.error("JWT token is unsupported: {}", e.getMessage());
            } catch (IllegalArgumentException e) {
                logger.error("JWT claims string is empty: {}", e.getMessage());
            } catch (SignatureException e) {
                logger.error("Invalid JWT signature: {}", e.getMessage());
            }
        }
        return true;
    }
}

