package com.projeto.financeiro.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
import com.projeto.financeiro.repository.RecuperacaoSenhaRepository;
import com.projeto.financeiro.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecuperacaoSenhaService {

    private static final int MAX_TENTATIVAS = 5;

    private static final String MENSAGEM_CODIGO_INVALIDO = "Código de recuperação inválido ou expirado.";

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
            return new SimpleMessageResponse("Se o email estiver cadastrado, um código de recuperação foi enviado.");
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

    @Transactional
    public SimpleMessageResponse resetarSenha(ResetPasswordRequest request) {
        // Busca o usuário e o pedido ativo SEM filtrar pelo código: é o que permite
        // contar tentativas erradas. Toda falha devolve a mesma mensagem genérica.
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException(MENSAGEM_CODIGO_INVALIDO));

        RecuperacaoSenha recuperacao = recuperacaoSenhaRepository
                .buscarAtivaPorUsuario(usuario)
                .orElseThrow(() -> new BadRequestException(MENSAGEM_CODIGO_INVALIDO));

        if (isExpirada(recuperacao)) {
            inativar(recuperacao);
            recuperacaoSenhaRepository.save(recuperacao);
            throw new BadRequestException(MENSAGEM_CODIGO_INVALIDO);
        }

        if (!codigoConfere(recuperacao.getCodigo(), request.codigo())) {
            recuperacao.setTentativas(recuperacao.getTentativas() + 1);
            if (recuperacao.getTentativas() >= MAX_TENTATIVAS) {
                inativar(recuperacao);
            }
            recuperacaoSenhaRepository.save(recuperacao);
            throw new BadRequestException(MENSAGEM_CODIGO_INVALIDO);
        }

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        // Invalida todos os tokens JWT emitidos antes da troca de senha.
        usuario.setTokenVersion(usuario.getTokenVersion() + 1);
        usuarioRepository.save(usuario);

        inativar(recuperacao);
        recuperacao.setDataUtilizacao(LocalDateTime.now());
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

    private void inativar(RecuperacaoSenha recuperacao) {
        recuperacao.setAtivo(false);
        recuperacao.setDataInativacao(LocalDateTime.now());
    }

    // Compara em tempo constante para não vazar o código por timing attack.
    private boolean codigoConfere(String esperado, String informado) {
        return MessageDigest.isEqual(
                esperado.getBytes(StandardCharsets.UTF_8),
                informado.getBytes(StandardCharsets.UTF_8));
    }
}
