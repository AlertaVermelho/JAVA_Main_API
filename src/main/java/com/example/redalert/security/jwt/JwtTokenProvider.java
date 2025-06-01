package com.example.redalert.security.jwt;

import com.example.redalert.config.JwtProperties;
import com.example.redalert.exception.JwtSecretNaoConfiguradoException;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtProperties jwtProperties;
    private SecretKey signingKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        String secret = jwtProperties.getSecret();

            if (secret == null || secret.isBlank() || secret.startsWith("${")) {
            String errorMessage = "ERRO CRÍTICO: O segredo JWT (app.jwt.secret) não está configurado ou não foi resolvido a partir das variáveis de ambiente. Verifique JWT_SECRET.";
            logger.error(errorMessage);
            throw new JwtSecretNaoConfiguradoException(errorMessage);
        }
        try {
            this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (Exception e) {
            String errorMessage = "ERRO CRÍTICO: O segredo JWT (app.jwt.secret) não é uma string Base64 válida.";
            logger.error(errorMessage, e);
            throw new JwtSecretNaoConfiguradoException(errorMessage);
        }
    }

    private SecretKey getSigningKey() {
        if (this.signingKey == null) {
            String errorMessage = "ERRO CRÍTICO: A chave de assinatura JWT (signingKey) é nula. O construtor falhou em inicializá-la ou o segredo não foi configurado.";
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        return this.signingKey;
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getEmailFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT inválido: {}", ex.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            logger.error("Assinatura do token JWT inválida: {}", ex.getMessage());
        }
        return false;
    }
}
