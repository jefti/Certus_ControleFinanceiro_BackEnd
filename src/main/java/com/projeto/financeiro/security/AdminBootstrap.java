package com.projeto.financeiro.security;

import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.Role;
import com.projeto.financeiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Promove a ADMIN, no startup, os e-mails configurados em ADMIN_EMAILS.
 * Decisoes de seguranca:
 *  - A API publica NUNCA define papel; a unica fonte de ADMIN e esta lista, fora do codigo.
 *  - Idempotente: roda a cada boot e so persiste quando o papel ainda nao e ADMIN.
 *  - Nao cria usuarios nem falha se o e-mail ainda nao existir; apenas registra um aviso.
 *    (o dono se cadastra normalmente como USER e e promovido no proximo boot)
 */
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UsuarioRepository usuarioRepository;

    @Value("${app.admin-emails:}")
    private String adminEmails;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<String> emails = parseEmails(adminEmails);
        if (emails.isEmpty()) {
            log.info("AdminBootstrap: nenhum e-mail configurado em ADMIN_EMAILS; nenhuma promocao realizada.");
            return;
        }

        for (String email : emails) {
            Optional<Usuario> encontrado = usuarioRepository.findByEmail(email);
            if (encontrado.isEmpty()) {
                log.warn("AdminBootstrap: e-mail '{}' configurado como admin nao existe ainda; sera promovido apos o cadastro.", email);
                continue;
            }
            Usuario usuario = encontrado.get();
            if (usuario.getRole() == Role.ADMIN) {
                continue;
            }
            usuario.setRole(Role.ADMIN);
            usuarioRepository.save(usuario);
            log.info("AdminBootstrap: usuario id={} promovido a ADMIN.", usuario.getId());
        }
    }

    private List<String> parseEmails(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> s.toLowerCase())
                .distinct()
                .toList();
    }
}
