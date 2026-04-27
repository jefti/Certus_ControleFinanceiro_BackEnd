package com.projeto.financeiro.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.financeiro.dto.request.ForgotPasswordRequest;
import com.projeto.financeiro.dto.request.ResetPasswordRequest;
import com.projeto.financeiro.dto.response.SimpleMessageResponse;
import com.projeto.financeiro.entity.RecuperacaoSenha;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.exception.NotFoundException;
import com.projeto.financeiro.repository.RecuperacaoSenhaRepository;
import com.projeto.financeiro.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecuperacaoSenhaService {

    private final UsuarioRepository usuarioRepository;
    private final RecuperacaoSenhaRepository recuperacaoSenhaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.password-recovery.expiration-minutes}")
    private long expirationMinutes;

    @Transactional
    public SimpleMessageResponse solicitarRecuperacao(ForgotPasswordRequest request) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(request.email());

        if (usuarioOptional.isEmpty()) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOptional.get();

        List<RecuperacaoSenha> recuperacoesAtivas =
                recuperacaoSenhaRepository.findAllByUsuarioAndAtivoTrue(usuario);

        inativarPedidosAtivos(recuperacoesAtivas);

        String codigo = gerarCodigo();

        RecuperacaoSenha recuperacaoSenha = RecuperacaoSenha.builder()
                .usuario(usuario)
                .codigo(codigo)
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .dataExpiracao(LocalDateTime.now().plusMinutes(expirationMinutes))
                .build();

        recuperacaoSenhaRepository.save(recuperacaoSenha);

        emailService.enviarCodigoRecuperacao(usuario.getEmail(), codigo);

        return new SimpleMessageResponse(
                "Se o email estiver cadastrado, um código de recuperação foi enviado."
        );
    }

    public SimpleMessageResponse resetarSenha(ResetPasswordRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado para o email informado."));

        RecuperacaoSenha recuperacao = recuperacaoSenhaRepository
                .findByUsuarioAndCodigoAndAtivoTrue(usuario, request.codigo())
                .orElseThrow(() -> new BadRequestException("Código de recuperação inválido."));

        if (isExpirada(recuperacao)) {
            recuperacao.setAtivo(false);
            recuperacao.setDataInativacao(LocalDateTime.now());
            recuperacaoSenhaRepository.save(recuperacao);

            throw new BadRequestException("Código de recuperação expirado.");
        }

        if (recuperacao.getDataUtilizacao() != null) {
            throw new BadRequestException("Código de recuperação já utilizado.");
        }

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);

        recuperacao.setAtivo(false);
        recuperacao.setDataUtilizacao(LocalDateTime.now());
        recuperacao.setDataInativacao(LocalDateTime.now());
        recuperacaoSenhaRepository.save(recuperacao);

        return new SimpleMessageResponse("Senha redefinida com sucesso.");
    }

    private String gerarCodigo() {
        SecureRandom random = new SecureRandom();
        int numero = random.nextInt(1_000_000);
        return String.format("%06d", numero);
    }

    private void inativarPedidosAtivos(List<RecuperacaoSenha> recuperacoes) {
        if (recuperacoes == null || recuperacoes.isEmpty()) {
            return;
        }

        LocalDateTime agora = LocalDateTime.now();

        for (RecuperacaoSenha recuperacao : recuperacoes) {
            recuperacao.setAtivo(false);
            recuperacao.setDataInativacao(agora);
        }

        recuperacaoSenhaRepository.saveAll(recuperacoes);
    }

    private boolean isExpirada(RecuperacaoSenha recuperacao) {
        return recuperacao.getDataExpiracao().isBefore(LocalDateTime.now());
    }
}
