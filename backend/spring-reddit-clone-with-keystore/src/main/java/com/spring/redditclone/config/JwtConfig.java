package com.spring.redditclone.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt.application")
@Data
@NoArgsConstructor
public class JwtConfig {
    private String secret;
    private String alias;
    private String tokenPrefix;
    private long validityPeriod;
    private String authorizationHeader;
}