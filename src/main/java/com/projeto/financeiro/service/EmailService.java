package com.projeto.financeiro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.projeto.financeiro.exception.EmailDeliveryException;

@Service
public class EmailService {

    private final RestClient restClient;
    private final String mailFrom;
    private final String resendApiKey;

    public EmailService(
            RestClient.Builder restClientBuilder,
            @Value("${app.mail.from}") String mailFrom,
            @Value("${app.mail.resend.api-key}") String resendApiKey,
            @Value("${app.mail.resend.base-url:https://api.resend.com}") String resendBaseUrl
    ) {
        this.mailFrom = mailFrom;
        this.resendApiKey = resendApiKey;
        this.restClient = restClientBuilder
                .baseUrl(resendBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + resendApiKey)
                .defaultHeader(HttpHeaders.USER_AGENT, "certus-financeiro-backend/1.0")
                .build();
    }

    public void enviarCodigoRecuperacao(String emailDestino, String codigo) {
        if (resendApiKey == null || resendApiKey.isBlank()) {
            throw new EmailDeliveryException("Servico de email nao configurado.");
        }

        ResendEmailRequest request = new ResendEmailRequest(
                mailFrom,
                List.of(emailDestino),
                "Codigo de recuperacao de senha",
                """
                <p>Ola!</p>
                <p>Seu codigo de recuperacao e: <strong>%s</strong></p>
                <p>Esse codigo expira em 15 minutos.</p>
                <p>Se voce nao solicitou esta recuperacao, ignore este email.</p>
                """.formatted(codigo),
                """
                Ola!

                Seu codigo de recuperacao e: %s

                Esse codigo expira em 15 minutos.

                Se voce nao solicitou esta recuperacao, ignore este email.
                """.formatted(codigo)
        );

        try {
            restClient.post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw new EmailDeliveryException("Falha ao enviar email de recuperacao.", e);
        } catch (RestClientException e) {
            throw new EmailDeliveryException("Falha ao enviar email de recuperacao.", e);
        }
    }

    private record ResendEmailRequest(
            String from,
            List<String> to,
            String subject,
            String html,
            String text
    ) {
    }
}
