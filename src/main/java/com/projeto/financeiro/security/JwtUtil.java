package com.projeto.financeiro.security;

import com.projeto.financeiro.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    @Value("${auth.jwt.secret}")
    private String jwtSecret;
    @Value("${auth.jwt-expiration}")
    private Long jwtExpirationMs;

    public String gerarToken(Authentication authentication) {
        Date dataExpircao = new Date(new Date().getTime() + jwtExpirationMs);
        Usuario usuario = (Usuario) authentication.getPrincipal();
        try {
            Key secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            // Gerar o token JWT usando a biblioteca JJWT
            return Jwts.builder()
                    .subject((usuario.getEmail()))
                    .expiration(dataExpircao)
                    .issuedAt(new Date())
                    .signWith(secretKey)
                    .compact();
        } catch (Exception e) {
            log.warn("Erro ao gerar token JWT", e);
            return "";
        }
    }

    // Método que sabe descobrir dentro do token com base na chave secreta, quais são as claims (informações) contidas no token
    private Claims getClaims(String token){
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception e) {
            log.warn("Erro ao obter claims do token JWT", e);
            return null;
        }
    }

    public String getUserName(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.getSubject() : null;
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