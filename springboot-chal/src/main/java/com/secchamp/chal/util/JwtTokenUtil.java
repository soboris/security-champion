package com.secchamp.chal.util;

import com.secchamp.chal.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // Generate token with user details as claims
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        
        // Add custom claims (excluding password)
        claims.put("userId", user.getUserId());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("phone", user.getPhone());
        claims.put("role", user.getRole());  // Add role to the token
        claims.put("login", true);  // Add login flag

        return createToken(claims, user.getUsername());
    }

    // Create the JWT token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // Token valid for 10 hours
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Extract claims from the token
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public Boolean isLoggedIn(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("login", Boolean.class);
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);  // Extract role from the token
    }
}
