package com.projeto.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.projeto.financeiro.dto.request.ForgotPasswordRequest;
import com.projeto.financeiro.dto.request.ResetPasswordRequest;
import com.projeto.financeiro.dto.response.SimpleMessageResponse;
import com.projeto.financeiro.entity.RecuperacaoSenha;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.exception.EmailDeliveryException;
import com.projeto.financeiro.repository.RecuperacaoSenhaRepository;
import com.projeto.financeiro.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class RecuperacaoSenhaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RecuperacaoSenhaRepository recuperacaoSenhaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RecuperacaoSenhaService recuperacaoSenhaService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recuperacaoSenhaService, "expirationMinutes", 15L);
    }

    @Test
    void shouldReturnNeutralMessageWhenEmailDoesNotExist() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("missing@email.com");

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        SimpleMessageResponse response = recuperacaoSenhaService.solicitarRecuperacao(request);

        assertNotNull(response);
        assertEquals(
                "Se o email estiver cadastrado, um código de recuperação foi enviado.",
                response.message());

        verify(recuperacaoSenhaRepository, never()).save(org.mockito.ArgumentMatchers.any(RecuperacaoSenha.class));
        verify(emailService, never()).enviarCodigoRecuperacao(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldCreatePasswordRecoveryRequestSuccessfully() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("john@email.com");
        Usuario user = buildUser(1L, "john@email.com");

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(recuperacaoSenhaRepository.findAllByUsuarioAndAtivoTrue(user)).thenReturn(List.of());

        SimpleMessageResponse response = recuperacaoSenhaService.solicitarRecuperacao(request);

        assertNotNull(response);
        assertEquals(
                "Se o email estiver cadastrado, um código de recuperação foi enviado.",
                response.message());

        verify(recuperacaoSenhaRepository).save(org.mockito.ArgumentMatchers.any(RecuperacaoSenha.class));
        verify(emailService).enviarCodigoRecuperacao(org.mockito.Mockito.eq("john@email.com"),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenRecoveryCodeIsInvalid() {
        ResetPasswordRequest request = new ResetPasswordRequest(
                "john@email.com",
                "999999",
                "newPassword123");

        Usuario user = buildUser(1L, "john@email.com");

        RecuperacaoSenha recovery = buildRecovery(
                user,
                "123456",
                true,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusMinutes(14),
                null,
                null);

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(recuperacaoSenhaRepository.buscarAtivaPorUsuario(user))
                .thenReturn(Optional.of(recovery));

        assertThrows(BadRequestException.class, () -> recuperacaoSenhaService.resetarSenha(request));

        // Código errado conta tentativa, mas (1 < 5) o pedido continua ativo.
        assertEquals(1, recovery.getTentativas());
        assertEquals(true, recovery.getAtivo());
        verify(recuperacaoSenhaRepository).save(recovery);
    }

    @Test
    void shouldDeactivateRecoveryAfterReachingMaxAttempts() {
        ResetPasswordRequest request = new ResetPasswordRequest(
                "john@email.com",
                "999999",
                "newPassword123");

        Usuario user = buildUser(1L, "john@email.com");

        RecuperacaoSenha recovery = buildRecovery(
                user,
                "123456",
                true,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusMinutes(14),
                null,
                null);
        recovery.setTentativas(4);

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(recuperacaoSenhaRepository.buscarAtivaPorUsuario(user))
                .thenReturn(Optional.of(recovery));

        assertThrows(BadRequestException.class, () -> recuperacaoSenhaService.resetarSenha(request));

        // A 5ª tentativa errada invalida o pedido.
        assertEquals(5, recovery.getTentativas());
        assertEquals(false, recovery.getAtivo());
        assertNotNull(recovery.getDataInativacao());
        verify(recuperacaoSenhaRepository).save(recovery);
    }

    @Test
    void shouldResetPasswordSuccessfullyWhenCodeIsValid() {
        ResetPasswordRequest request = new ResetPasswordRequest(
                "john@email.com",
                "123456",
                "newPassword123");

        Usuario user = buildUser(1L, "john@email.com");

        RecuperacaoSenha recovery = buildRecovery(
                user,
                "123456",
                true,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusMinutes(14),
                null,
                null);

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(recuperacaoSenhaRepository.buscarAtivaPorUsuario(user))
                .thenReturn(Optional.of(recovery));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encoded-new");

        SimpleMessageResponse response = recuperacaoSenhaService.resetarSenha(request);

        assertEquals("Senha redefinida com sucesso.", response.message());
        assertEquals("encoded-new", user.getSenha());
        assertEquals(1, user.getTokenVersion());
        assertEquals(false, recovery.getAtivo());
        assertNotNull(recovery.getDataUtilizacao());
        verify(usuarioRepository).save(user);
        verify(recuperacaoSenhaRepository).save(recovery);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenRecoveryCodeIsExpired() {
        ResetPasswordRequest request = new ResetPasswordRequest(
                "john@email.com",
                "123456",
                "newPassword123");

        Usuario user = buildUser(1L, "john@email.com");

        RecuperacaoSenha recovery = buildRecovery(
                user,
                "123456",
                true,
                LocalDateTime.now().minusMinutes(20),
                LocalDateTime.now().minusMinutes(1),
                null,
                null);

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(recuperacaoSenhaRepository.buscarAtivaPorUsuario(user))
                .thenReturn(Optional.of(recovery));

        assertThrows(BadRequestException.class, () -> recuperacaoSenhaService.resetarSenha(request));

        assertEquals(false, recovery.getAtivo());
        assertNotNull(recovery.getDataInativacao());

        verify(recuperacaoSenhaRepository).save(recovery);
    }

    @Test
    void shouldPropagateEmailDeliveryExceptionWhenEmailProviderFails() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("john@email.com");
        Usuario user = buildUser(1L, "john@email.com");

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(recuperacaoSenhaRepository.findAllByUsuarioAndAtivoTrue(user)).thenReturn(List.of());
        org.mockito.Mockito.doThrow(new EmailDeliveryException("Falha ao enviar email de recuperacao."))
                .when(emailService)
                .enviarCodigoRecuperacao(org.mockito.Mockito.eq("john@email.com"), org.mockito.ArgumentMatchers.anyString());

        EmailDeliveryException exception = assertThrows(
                EmailDeliveryException.class,
                () -> recuperacaoSenhaService.solicitarRecuperacao(request));

        assertEquals("Falha ao enviar email de recuperacao.", exception.getMessage());
        verify(recuperacaoSenhaRepository).save(org.mockito.ArgumentMatchers.any(RecuperacaoSenha.class));
        verify(emailService).enviarCodigoRecuperacao(
                org.mockito.Mockito.eq("john@email.com"),
                org.mockito.ArgumentMatchers.anyString());
    }

    private Usuario buildUser(Long id, String email) {
        Usuario user = new Usuario();
        user.setId(id);
        user.setNome("John Doe");
        user.setEmail(email);
        user.setSenha("encoded-password");
        user.setCelular("99999999999");
        user.setDataCriacao(Instant.now());
        return user;
    }

    private RecuperacaoSenha buildRecovery(
            Usuario user,
            String code,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            LocalDateTime inactivatedAt,
            LocalDateTime usedAt) {
        return RecuperacaoSenha.builder()
                .id(1L)
                .usuario(user)
                .codigo(code)
                .ativo(active)
                .dataCriacao(createdAt)
                .dataExpiracao(expiresAt)
                .dataInativacao(inactivatedAt)
                .dataUtilizacao(usedAt)
                .build();
    }

}
