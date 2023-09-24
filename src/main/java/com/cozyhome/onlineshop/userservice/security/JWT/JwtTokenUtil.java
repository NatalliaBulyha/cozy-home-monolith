package com.cozyhome.onlineshop.userservice.security.JWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.cozyhome.onlineshop.userservice.security.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenUtil {

	@Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.token.validity}")
    private long tokenValiditi;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        log.info("[ON getAllClaimsFromToken]:: getting all claims from token using Jwts parser");
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            log.error("[ON getAllClaimsFromToken]:: token is expired");
            throw new InvalidTokenException(e.getLocalizedMessage(), e);
        }
        return claims;
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap();
        String token = doGenerateToken(claims, username);
        log.info("[ON generateToken]:: token has generated successfully");
        return token;
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        log.info("[ON doGenerateToken]:: token generating...");
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValiditi))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        log.info("[ON validateToken]:: token validation...");
        final String username = getUsernameFromToken(token);
        boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        log.info("[ON validateToken]:: is token valid - {}", isValid);
        return isValid;
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        boolean isExpired = expiration.before(new Date());
        log.info("[ON isTokenExpired]:: is token expired - {}", isExpired);
        return isExpired;
    }
}
