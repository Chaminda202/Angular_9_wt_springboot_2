package com.spring.redditclone.jwt;

import com.spring.redditclone.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.KeyStore;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private KeyStore keyStore;

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(this.secretKey)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(this.jwtConfig.getValidityPeriod() * 1000)))
                .compact();
    }

    public String generateTokenByUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .signWith(this.secretKey)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(this.jwtConfig.getValidityPeriod() * 1000)))
                .compact();
    }


    public boolean validateToken(String jwt) {
        Jwts.parser()
            .setSigningKey(this.secretKey).parseClaimsJws(jwt);
        return true;
    }

    public String getUsernameJwt(String jwt) {
        Claims claims = Jwts
                .parser()
                .setSigningKey(this.secretKey)
                .parseClaimsJws(jwt)
                .getBody();
        return claims.getSubject();
    }
}
