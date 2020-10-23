package com.nuguri.example.util;

import com.nuguri.example.entity.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.validity}")
    private int validity;

    @Value("${jwt.type}")
    private String type;

    public Claims generateClaims(Account account) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", account.getId());
        map.put("email", account.getEmail());
        map.put("role", account.getRole());
        DefaultClaims claims = new DefaultClaims(map);
        return claims;
    }

    public String generateJwtToken(Account account) {
        long timeMillis = System.currentTimeMillis();
        return Jwts
                .builder()
                .setId(UUID.randomUUID().toString())
                .setClaims(generateClaims(account))
                .setIssuedAt(new Date())
                .setExpiration(new Date(timeMillis + validity * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

    }

    public Claims getClaimsFromToken(String token) {
        token = token.substring(7);
        return Jwts
                .parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isJwtTokenExpired(Claims claims) {
        return claims
                .getExpiration()
                .before(new Date());
    }

    public boolean isJwtTokenExpired(String token) {
        return isJwtTokenExpired(getClaimsFromToken(token));
    }

}
