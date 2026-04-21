package com.projeto.financeiro.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projeto.financeiro.dto.request.ForgotPasswordRequest;
import com.projeto.financeiro.dto.request.ResetPasswordRequest;
import com.projeto.financeiro.dto.response.SimpleMessageResponse;
import com.projeto.financeiro.entity.RecuperacaoSenha;
import com.projeto.financeiro.repository.RecuperacaoSenhaRepository;
import com.projeto.financeiro.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecuperacaoSenhaService {

    private final UsuarioRepository usuarioRepository;
    private final RecuperacaoSenhaRepository recuperacaoSenhaRepository;
    private final PasswordEncoder passwordEncoder;

    public SimpleMessageResponse solicitarRecuperacao(ForgotPasswordRequest request){
        return new SimpleMessageResponse("Em construção!");
    }
    
    public SimpleMessageResponse resetarSenha(ResetPasswordRequest request){
        return new SimpleMessageResponse("Em construção!");
    }

    private String gerarCodigo() {
        return "Codigo";
    }

    private void inativarPedidosAtivos(List<RecuperacaoSenha> recuperacoes) {
        
    }

    private boolean isExpirada(RecuperacaoSenha recuperacao) {
        return false;
    }

}