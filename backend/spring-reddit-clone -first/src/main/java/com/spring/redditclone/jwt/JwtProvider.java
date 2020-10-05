package com.spring.redditclone.jwt;

import com.spring.redditclone.exception.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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

@Component
public class JwtProvider {
    private KeyStore keyStore;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/jwt.jks");
            keyStore.load(resourceAsStream, "testpassword".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred while loading keystore", e);
        }
    }

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
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
            return keyStore.getCertificate("asymetric").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("Exception occurred while " +
                    "retrieving public key from keystore", e);
        }
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("asymetric", "testpassword".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occurred while retrieving public key from keystore", e);
        }
    }
}
