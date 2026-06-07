package com.fresh.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    @Value("${auth.token.secret}")
    private String secret;

    @Value("${auth.token.expire}")
    private Long expire;

    @Value("${auth.token.refresh-expire}")
    private Long refreshExpire;

    @Value("${auth.token.prefix}")
    private String tokenPrefix;

    @Value("${auth.token.refresh-prefix}")
    private String refreshPrefix;

    private final StringRedisTemplate stringRedisTemplate;

    public JwtUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        String tokenId = UUID.randomUUID().toString().replace("-", "");
        String token = Jwts.builder()
                .setId(tokenId)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expire * 1000))
                .signWith(getSecretKey())
                .compact();
        stringRedisTemplate.opsForValue().set(tokenPrefix + tokenId, userId.toString(), expire, TimeUnit.SECONDS);
        return token;
    }

    public String generateRefreshToken(Long userId) {
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(refreshPrefix + refreshToken, userId.toString(), refreshExpire, TimeUnit.SECONDS);
        return refreshToken;
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            String tokenId = claims.getId();
            Boolean hasKey = stringRedisTemplate.hasKey(tokenPrefix + tokenId);
            return hasKey != null && hasKey;
        } catch (Exception e) {
            return false;
        }
    }

    public void removeToken(String token) {
        try {
            Claims claims = parseToken(token);
            String tokenId = claims.getId();
            stringRedisTemplate.delete(tokenPrefix + tokenId);
        } catch (Exception ignored) {
        }
    }

    public Long getUserIdFromRefreshToken(String refreshToken) {
        String userIdStr = stringRedisTemplate.opsForValue().get(refreshPrefix + refreshToken);
        if (userIdStr == null) {
            return null;
        }
        return Long.parseLong(userIdStr);
    }

    public void removeRefreshToken(String refreshToken) {
        stringRedisTemplate.delete(refreshPrefix + refreshToken);
    }

    public Long getExpire() {
        return expire;
    }
}
