package com.projeto.financeiro.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from}")
    private String mailFrom;

    public void enviarCodigoRecuperacao(String emailDestino, String codigo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(emailDestino);
        message.setSubject("Código de recuperação de senha");
        message.setText(
                "Olá!\n\n" +
                "Seu código de recuperação de senha é: " + codigo + "\n\n" +
                "Esse código expira em 15 minutos.\n\n" +
                "Se você não solicitou esta recuperação, ignore este email."
        );

        javaMailSender.send(message);
    }
    
}
