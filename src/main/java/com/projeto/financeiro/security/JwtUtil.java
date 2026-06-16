package com.projeto.financeiro.security;

import com.projeto.financeiro.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {
    @Value("${auth.jwt.secret}")
    private String jwtSecret;
    @Value("${auth.jwt.expiration}")
    private Long jwtExpirationMs;
    @Value("${auth.jwt.issuer}")
    private String jwtIssuer;
    @Value("${auth.jwt.audience}")
    private String jwtAudience;

    @PostConstruct
    public void validateJwtSecret() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET_KEY deve ter pelo menos 32 caracteres");
        }
    }

    public String gerarToken(Authentication authentication) {
        Date dataExpircao = new Date(new Date().getTime() + jwtExpirationMs);
        Usuario usuario = (Usuario) authentication.getPrincipal();
        try {
            Key secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            // Gerar o token JWT usando a biblioteca JJWT
            return Jwts.builder()
                    .id(UUID.randomUUID().toString())
                    .subject(usuario.getEmail())
                    .issuer(jwtIssuer)
                    .audience().add(jwtAudience).and()
                    .claim("tv", usuario.getTokenVersion())
                    .issuedAt(new Date())
                    .expiration(dataExpircao)
                    .signWith(secretKey)
                    .compact();
        } catch (Exception e) {
            log.error("Erro ao gerar token JWT", e);
            throw new IllegalStateException("Falha ao gerar token JWT", e);
        }
    }

    // Método que sabe descobrir dentro do token com base na chave secreta, quais são as claims (informações) contidas no token
    private Claims getClaims(String token){
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(jwtIssuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Set<String> audiences = claims.getAudience();
            if (audiences == null || !audiences.contains(jwtAudience)) {
                log.warn("Token JWT com audience invalida");
                return null;
            }
            return claims;

        } catch (Exception e) {
            log.warn("Erro ao obter claims do token JWT", e);
            return null;
        }
    }

    public String getUserName(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    public Integer getTokenVersion(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.get("tv", Integer.class) : null;
    }

    public boolean isValidToken(String token){
        Claims claims = getClaims(token);
        if (claims == null) {
            return false;
        }
        String email = claims.getSubject();
        Date dataExpiracao = claims.getExpiration();
        Date dataAtual = new Date(System.currentTimeMillis());
        return email != null && dataExpiracao != null && dataAtual.before(dataExpiracao);
    }
}
