package com.spring.redditclone.jwt;

import com.spring.redditclone.config.JwtConfig;
import com.spring.redditclone.exception.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtConfig jwtConfig;
    private KeyStore keyStore;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/jwt.jks");
            keyStore.load(resourceAsStream, this.jwtConfig.getSecret().toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred while loading keystore", e);
        }
    }

    /*public String generateToken(Authentication authentication) {
        //token validity period
        long nowMillis = System.currentTimeMillis();
        Date expireDate = new Date(nowMillis + this.jwtConfig.getValidityPeriod() * 1000);

        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .compact();
    }*/

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(this.jwtConfig.getValidityPeriod() * 1000)))
                .compact();
    }

    public String generateTokenByUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .signWith(getPrivateKey())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(this.jwtConfig.getValidityPeriod() * 1000)))
                .compact();
    }


    public boolean validateToken(String jwt) {
        Jwts.parser()
            .setSigningKey(getPublickey()).parseClaimsJws(jwt);
        return true;
    }

    public String getUsernameJwt(String jwt) {
        Claims claims = Jwts
                .parser()
                .setSigningKey(getPublickey())
                .parseClaimsJws(jwt)
                .getBody();
        return claims.getSubject();
    }

    private PublicKey getPublickey() {
        try {
            return keyStore.getCertificate(this.jwtConfig.getAlias()).getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("Exception occurred while " +
                    "retrieving public key from keystore", e);
        }
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey(this.jwtConfig.getAlias(), this.jwtConfig.getSecret().toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occurred while retrieving public key from keystore", e);
        }
    }
}
