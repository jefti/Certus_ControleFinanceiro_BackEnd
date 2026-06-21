package com.projeto.financeiro.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting por IP nos endpoints públicos sensíveis a abuso/brute force
 * (recuperação de senha e cadastro). O login tem seu próprio filtro dedicado
 * ({@link LoginRateLimitFilter}).
 *
 * <p>Cada endpoint mantém um orçamento de requisições independente por IP, para
 * que o consumo de um caminho não derrube os demais.</p>
 */
public class SensitiveEndpointRateLimitFilter extends OncePerRequestFilter {

    private record Limite(int capacidade, Duration janela) {
        Bandwidth toBandwidth() {
            return Bandwidth.builder()
                    .capacity(capacidade)
                    .refillIntervally(capacidade, janela)
                    .build();
        }
    }

    /**
     * Limites por endpoint:
     * <ul>
     *   <li>{@code /api/auth/forgot-password}: dispara envio de e-mail (custo externo)
     *       e é vetor de enumeração/flood → restrito.</li>
     *   <li>{@code /api/auth/reset-password}: brute force do código de 6 dígitos
     *       → restrito (defesa em profundidade somada ao contador de tentativas).</li>
     *   <li>{@code /api/usuarios/cadastrar}: criação automatizada de contas → restrito.</li>
     * </ul>
     */
    private static final Map<String, Limite> LIMITES_POR_PATH = Map.of(
            "/api/auth/forgot-password", new Limite(5, Duration.ofMinutes(15)),
            "/api/auth/reset-password", new Limite(10, Duration.ofMinutes(15)),
            "/api/usuarios/cadastrar", new Limite(5, Duration.ofHours(1))
    );

    private final Map<String, Bucket> bucketsPorChave = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod())
                || !LIMITES_POR_PATH.containsKey(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        String chave = request.getRemoteAddr() + "|" + path;
        Bucket bucket = bucketsPorChave.computeIfAbsent(chave, ignored -> newBucket(path));

        if (!bucket.tryConsume(1)) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Limite de requisições excedido. Tente novamente mais tarde.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Bucket newBucket(String path) {
        return Bucket.builder()
                .addLimit(LIMITES_POR_PATH
                        .get(path)
                        .toBandwidth())
                .build();
    }
}
